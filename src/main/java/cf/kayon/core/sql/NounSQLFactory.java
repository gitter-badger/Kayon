/*
 * Kayon
 * Copyright (C) 2015 Ruben Anders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cf.kayon.core.sql;

import cf.kayon.core.CaseHandling;
import cf.kayon.core.Contexed;
import cf.kayon.core.Gender;
import cf.kayon.core.KayonContext;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounDeclensionUtil;
import cf.kayon.core.noun.NounForm;
import cf.kayon.core.util.StringUtil;
import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.typesafe.config.ConfigException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Used to perform database actions with nouns.
 * <p>
 * Thread-safe.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class NounSQLFactory extends Contexed
{
    /*
     * Thread safety notice
     *
     * All set fields are final, guaranteeing memory visibility.
     */

    /**
     * Constructs a new instance.
     * <p>
     * All statements are retrieved from the config of the context at construct time. The statements are also compiled at construct time.
     *
     * @param context The {@link KayonContext} for this instance.
     * @since 0.2.0
     */
    public NounSQLFactory(@NotNull KayonContext context)
    {
        super(context);
        insertSql = context.getConfig().getString("database.statements.insert");
        querySql = context.getConfig().getString("database.statements.query");
        setupSql = context.getConfig().getString("database.statements.setup");
    }

    /**
     * Compiles the SQL statements into PreparedStatement objects. Required for later calls to {@link #queryNouns(String)} or {@link #saveNounToDatabase(Noun)}.
     * <p>
     * <strong>This method depends on {@link #setupDatabaseForNouns()}.</strong>
     *
     * @since 0.2.0
     */
    public void compileStatements()
    {
        String currentPath = null;
        try
        {
            currentPath = "database.statements.insert";
            insertStatement = getContext().getConnection().prepareStatement(insertSql);
            currentPath = "database.statements.query";
            queryStatement = getContext().getConnection().prepareStatement(querySql);
        } catch (SQLException e)
        {
            throw new ConfigException.BadValue(getContext().getConfig().origin(), currentPath, "See cause below!", e);
        }
    }


    /**
     * The SQL string for inserting a {@link Noun} into a database.
     *
     * @since 0.0.1
     */
    private final String insertSql;

    /**
     * The SQL statement for inserting a {@link Noun} into a database.
     *
     * @since 0.2.0
     */
    private volatile PreparedStatement insertStatement;

    /**
     * The SQL string for querying {@link Noun}s by a finite form from a database.
     *
     * @since 0.0.1
     */
    private final String querySql;

    /**
     * The SQL statement for querying {@link Noun}s by a finite form from a database.
     *
     * @since 0.2.0
     */
    private volatile PreparedStatement queryStatement;

    /**
     * The SQL string for setting up a database for noun operations.
     *
     * @since 0.0.1
     */
    private final String setupSql;

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object.
     */

    /**
     * Saves a noun to the database.
     * <p>
     * If the specified noun did not have a UUID before, it gets a random UUID assigned.
     *
     * @param noun The noun to save.
     * @throws SQLException If there are any issues when executing the SQL update against the database connection.
     * @since 0.0.1
     */
    public void saveNounToDatabase(Noun noun) throws SQLException
    {
        synchronized (getContext().getConnection())
        {
            insertStatement.setString(1, noun.getRootWord());
            UUID uuid = noun.getUuid();
            if (uuid == null)
            {
                uuid = UUID.randomUUID();
                noun.initializeUuid(uuid);
            }
            insertStatement.setObject(2, uuid.toString());
            insertStatement.setByte(3, SQLUtil.idForGender(noun.getGender()));
            if (noun.getNounDeclension() != null)
                insertStatement.setString(4, noun.getNounDeclension().getClass().getName()); // Full class name
            else
                insertStatement.setString(4, null);
            insertStatement.setObject(5, noun.getTranslations());
            int counter = 6;
            for (NounForm nounForm : NounForm.values())
            {
                @Nullable
                String formOrNull = noun.getForm(nounForm);
                @Nullable
                String definedForm = noun.getDefinedForm(nounForm);

                insertStatement.setString(counter + 12, definedForm);
                insertStatement.setString(counter++, formOrNull);
            }
            insertStatement.executeUpdate();
        }
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object.
     *
     * Because ResultSets are still backed by the connection and are not just collections that hold everything in memory,
     * locking is required.
     */

    /**
     * Constructs a {@link Noun} out of the currently selected row of a {@link ResultSet}.
     * <p>
     * The passed {@link ResultSet} will not be closed by this method. It is the task of the caller to close the {@link ResultSet} after it is done with all operations.
     *
     * @param resultSet The {@link ResultSet} with the row selected to read from.
     * @return A reconstructed {@link Noun}.
     * @throws SQLException         If any errors occur when reading from the {@link ResultSet}.
     * @throws NullPointerException If the specified {@code resultSet} is {@code null}.
     * @since 0.0.1
     */
    @NotNull
    @SuppressWarnings("unchecked")
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Noun constructNounFromResultSet(@NotNull ResultSet resultSet) throws SQLException
    {
        synchronized (getContext().getConnection())
        {
            @NotNull
            String rootWord = resultSet.getString(1);
            @NotNull
            UUID uuid = (UUID) resultSet.getObject(2);
            @NotNull
            Gender gender = SQLUtil.genderForId(resultSet.getByte(3));
            NounDeclension nounDeclension = NounDeclensionUtil.forName(resultSet.getString(4));
            Map<Locale, String> translations = (Map<Locale, String>) resultSet.getObject(5);
            Noun noun = new Noun(getContext(), nounDeclension, gender, rootWord);
            noun.setTranslations(translations);
            noun.initializeUuid(uuid);
            int counter = 18;
            for (NounForm nounForm : NounForm.values())
            {
                @Nullable
                String formOrNull = resultSet.getString(counter++);
                noun.setDefinedForm(nounForm, formOrNull);
            }
            return noun;
        }
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object.
     */

    /**
     * Gets a {@link Set} of {@link Noun}s out of a database connection by the specified form.
     * Searches in the table {@code NOUNS}.
     *
     * @param formToSearch The form to search. May be any kind of special form. Should not contain uppercase characters.
     * @return A {@link Set} of {@link Noun}s. May be empty if no nouns have been found.
     * @throws SQLException         If a error in executing the query against the database connection occurs.
     * @throws NullPointerException If {@code connection} or {@code formToSearch} is {@code null}.
     * @since 0.0.1
     */
    @NotNull
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public List<Noun> queryNouns(@NotNull String formToSearch) throws SQLException
    {
        synchronized (getContext().getConnection())
        {
            return queryNounsFromRegex(StringUtil.anySpecialRegex(formToSearch));
        }
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object. (only for the time of database operations)
     */

    /**
     * Gets a {@link Set} of {@link Noun}s out of a database connection by the specified form.
     * Searches in the table {@code NOUNS}.
     *
     * @param regex The form's regular expression as returned by {@link StringUtil#anySpecialRegex(String)}.
     * @return A {@link Set} of {@link Noun}s. May be empty if no nouns have been found.
     * @throws SQLException         If a error in executing the query occurs.
     * @throws NullPointerException If {@code connection} or {@code regex} is {@code null}.
     * @since 0.0.1
     */
    @NotNull
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private List<Noun> queryNounsFromRegex(@NotNull String regex) throws SQLException
    {

        checkNotNull(regex);
        ArrayList<Noun> list = Lists.newArrayList();
        synchronized (getContext().getConnection())
        {
            queryStatement.setString(1, regex);
            Pattern pattern = Pattern.compile(regex);
            try (ResultSet results = queryStatement.executeQuery())
            {
                while (results.next())
                {
                    Noun currentResult = constructNounFromResultSet(results);
                    for (NounForm nounForm : NounForm.values())
                    {
                        String form = currentResult.getForm(nounForm);
                        if (form != null && pattern.matcher(form).matches()) // If a matching form has been found, short-circuit
                        {
                            list.add(currentResult);
                            break; // break out of nested loop
                        }
                    }

                }
            }
            return list;
        }
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object.
     */

    /**
     * Makes sure that the {@code NOUNS} table exists in the specified connection to a database.
     * <p>
     * If the {@code NOUNS} table already exists, nothing is changed.
     *
     * @throws SQLException         If there were any errors when executing the SQL statements.
     * @throws NullPointerException If the {@code connection} is null.
     * @since 0.0.1
     */
    public void setupDatabaseForNouns() throws SQLException
    {
        synchronized (getContext().getConnection())
        {
            try (Statement statement = getContext().getConnection().createStatement())
            {
                statement.execute(setupSql);
            }
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof NounSQLFactory)) return false;
        if (!super.equals(o)) return false;
        NounSQLFactory that = (NounSQLFactory) o;
        return com.google.common.base.Objects.equal(insertSql, that.insertSql) &&
               Objects.equal(insertStatement, that.insertStatement) &&
               Objects.equal(querySql, that.querySql) &&
               Objects.equal(queryStatement, that.queryStatement) &&
               Objects.equal(setupSql, that.setupSql);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(super.hashCode(), insertSql, insertStatement, querySql, queryStatement, setupSql);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("insertSql", insertSql)
                          .add("insertStatement", insertStatement)
                          .add("querySql", querySql)
                          .add("queryStatement", queryStatement)
                          .add("setupSql", setupSql)
                          .toString();

    }
}
