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
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.typesafe.config.ConfigException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
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

    /**
     * The SQL string for inserting a {@link Noun} into a database.
     *
     * @since 0.0.1
     */
    private final String insertSql;
    /**
     * The SQL string for querying {@link Noun}s by a finite form from a database.
     *
     * @since 0.0.1
     */
    private final String querySql;
    /**
     * The SQL string for setting up a database for noun operations.
     *
     * @since 0.0.1
     */
    private final String setupSql;
    /**
     * The SQL string for searching for querying {@link Noun}s by their root word.
     *
     * @since 0.2.3
     */
    private final String rootQuerySql;
    /**
     * The SQL statement for inserting a {@link Noun} into a database.
     *
     * @since 0.2.0
     */
    private volatile PreparedStatement insertStatement;
    /**
     * The SQL statement for querying {@link Noun}s by a finite form from a database.
     *
     * @since 0.2.0
     */
    private volatile PreparedStatement queryStatement;
    /**
     * The SQL statement for searching for querying {@link Noun}s by their root word.
     *
     * @since 0.2.3
     */
    private volatile PreparedStatement rootQueryStatement;

    /**
     * Constructs a new instance.
     * <p>
     * All statements are retrieved from the config of the context at construct time. The statements are compiled later by calling {@link #compileStatements()}.
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
        rootQuerySql = context.getConfig().getString("database.statements.rootQuery");
    }

    /**
     * Compiles the SQL statements into {@link PreparedStatement} objects.
     * Required for later calls to {@link #queryNouns(String, BlockingQueue)} or {@link #saveNounToDatabase(Noun)}.
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
            synchronized (getContext().getConnection())
            {
                currentPath = "database.statements.insert";
                insertStatement = getContext().getConnection().prepareStatement(insertSql);
                currentPath = "database.statements.query";
                queryStatement = getContext().getConnection().prepareStatement(querySql);
                currentPath = "database.statements.rootQuery";
                rootQueryStatement = getContext().getConnection().prepareStatement(rootQuerySql);
            }
        } catch (SQLException e)
        {
            throw new ConfigException.BadValue(getContext().getConfig().origin(), currentPath,
                                               "See cause below (Invalid SQL statement in config could not be compiled)!", e);
        }
    }

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
     * @throws SQLException         If there are any issues when executing the SQL update against the database connection.
     * @throws NullPointerException If {@code noun} is {@code null}.
     * @since 0.0.1
     */
    public void saveNounToDatabase(@NotNull Noun noun) throws SQLException
    {
        checkNotNull(noun);
        saveNounToDatabase(noun, false);
    }

    /**
     * Saves a noun to the database or adds the insert statement to the statement batch.
     * <p>
     * If the specified noun did not have UUID before, it gets a random UUID assigned.
     * <p>
     * Thread safety notice:
     * External synchronization may be necessary to prevent other threads from messing with the batch created by this method.
     * External code should lock on {@link KayonContext#getConnection()}, like this:
     * <pre>{@code
     * Noun[] nouns = ...;
     *
     * synchronized(getContext().getConnection())
     * {
     *     for(int i = 0; i < nouns.length; i++)
     *     {
     *         getContext().getNounSQLFactory()
     *                     .saveNounToDatabase(nouns[i],
     *                                         i + 1 != nouns.length));
     *     }
     * }
     * }</pre>
     *
     * @param noun    The noun to save.
     * @param doBatch {@code true} to add the insert statement to the batch or {@code false} if the statement should
     *                be executed now (will also execute any old statements added to the batch).
     * @throws SQLException If there are any issues when executing the SQL update against the database connection.
     * @since 0.2.3
     */
    @Contract("null, true -> fail")
    public void saveNounToDatabase(@Nullable Noun noun, boolean doBatch) throws SQLException
    {
        synchronized (getContext().getConnection())
        {
            if (noun != null)
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
                insertStatement.addBatch();
            } else if (doBatch)
                throw new IllegalArgumentException("noun == null and doBatch == true not allowed");
            if (!doBatch)
                insertStatement.executeBatch();
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
     * The passed {@link ResultSet} will not be closed by this method.
     * It is the task of the caller to close the {@link ResultSet} after it is done with all operations.
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
     * Queries the {@link Noun}s out of a database connection by the specified form.
     * Searches in the table {@code NOUNS} (Unless the application is configured differently).
     *
     * @param formToSearch The form to search. May be any kind of special form (and may be raw user input).
     * @param writeTo      The {@link BlockingQueue} to write the resulting {@link Noun}s to.
     * @throws SQLException         If a error in executing the query occurs.
     * @throws InterruptedException If a write to the BlockingQueue was interrupted.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If {@code formToSearch} is {@link String#isEmpty() empty}.
     * @since 0.2.3
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    public void queryNouns(@NotNull String formToSearch, @NotNull BlockingQueue<? super Noun> writeTo) throws SQLException, InterruptedException
    {
        checkNotEmpty(formToSearch);
        checkNotNull(writeTo);

        // 1. MAnŪs -> manūs
        // 2. manūs -> man[uūŭ]s
        String regex = StringUtil.anySpecialRegex(formToSearch.toLowerCase());
        queryNounsFromRegex(regex, writeTo);
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object. (only for the time of database operations)
     */

    /**
     * Queries the {@link Noun}s out of a database connection by the specified form.
     * Searches in the table {@code NOUNS} (Unless the application is configured differently).
     *
     * @param regex   The form's regular expression (typically as returned by {@link StringUtil#anySpecialRegex(String)}).
     * @param writeTo The {@link BlockingQueue} to write the resulting {@link Noun}s to.
     * @throws SQLException             If a error in executing the query occurs.
     * @throws InterruptedException     If a write to the BlockingQueue was interrupted.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If {@code regex} is {@link String#isEmpty() empty}.
     * @since 0.2.3
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void queryNounsFromRegex(@NotNull String regex, @NotNull BlockingQueue<? super Noun> writeTo) throws SQLException, InterruptedException
    {
        checkNotEmpty(regex);
        checkNotNull(writeTo);
        synchronized (getContext().getConnection())
        {
            queryStatement.setString(1, regex);
            Pattern pattern = Pattern.compile(regex);
            try (ResultSet results = queryStatement.executeQuery())
            {
                while (results.next())
                {
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    Noun currentResult = constructNounFromResultSet(results);
                    for (NounForm nounForm : NounForm.values())
                    {
                        String form = currentResult.getForm(nounForm);
                        if (form != null && pattern.matcher(form).matches())
                        {
                            writeTo.put(currentResult);
                            break; // break out of nested for iteration loop, jump to next result
                        }
                    }

                }
            }
        }
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object.
     */

    /**
     * Queries the {@link Noun}s out of a database connection by the specified root word.
     * Searches in the table {@code NOUNS} (Unless the application is configured differently).
     *
     * @param rootWordToSearch The root word to search. May be any kind of special form (and may be raw user input).
     * @param writeTo          The {@link BlockingQueue} to write the resulting {@link Noun}s to.
     * @throws SQLException         If a error in executing the query occurs.
     * @throws InterruptedException If a write to the BlockingQueue was interrupted.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If {@code rootWordToSearch} is {@link String#isEmpty() empty}.
     * @since 0.2.3
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    public void queryNounsByRootWord(@NotNull String rootWordToSearch, @NotNull BlockingQueue<? super Noun> writeTo) throws SQLException, InterruptedException
    {
        checkNotEmpty(rootWordToSearch);
        checkNotNull(writeTo);

        // 1. MAnŪs -> manūs
        // 2. manūs -> man[uūŭ]s
        String regex = StringUtil.anySpecialRegex(rootWordToSearch.toLowerCase());
        queryNounsByRootWordRegex(regex, writeTo);
    }

    /*
     * Thread safety notice
     *
     * Method is synchronized on the connection object. (only for the time of database operations)
     */

    /**
     * Queries the {@link Noun}s out of a database connection by the specified root word.
     * Searches in the table {@code NOUNS} (Unless the application is configured differently).
     *
     * @param regexRootWord The root word's regular expression (typically as returned by {@link StringUtil#anySpecialRegex(String)}).
     * @param writeTo       The {@link BlockingQueue} to write the resulting {@link Noun}s to.
     * @throws SQLException             If a error in executing the query occurs.
     * @throws InterruptedException     If a write to the BlockingQueue was interrupted.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If {@code regexRootWord} is {@link String#isEmpty() empty}.
     * @since 0.2.3
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void queryNounsByRootWordRegex(@NotNull String regexRootWord, @NotNull BlockingQueue<? super Noun> writeTo) throws SQLException, InterruptedException
    {
        checkNotEmpty(regexRootWord);
        checkNotNull(writeTo);
        synchronized (getContext().getConnection())
        {
            rootQueryStatement.setString(1, regexRootWord);
            try (ResultSet results = rootQueryStatement.executeQuery())
            {
                while (results.next())
                {
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    Noun currentResult = constructNounFromResultSet(results);
                    writeTo.put(currentResult);
                }
            }
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
     * @throws SQLException If there were any errors when executing the SQL statements.
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

    /**
     * @since 0.2.0
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof NounSQLFactory)) return false;
        if (!super.equals(o)) return false;
        NounSQLFactory that = (NounSQLFactory) o;
        return Objects.equal(insertSql, that.insertSql) &&
               Objects.equal(querySql, that.querySql) &&
               Objects.equal(setupSql, that.setupSql) &&
               Objects.equal(rootQuerySql, that.rootQuerySql) &&
               Objects.equal(insertStatement, that.insertStatement) &&
               Objects.equal(queryStatement, that.queryStatement);
    }

    /**
     * @since 0.2.0
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(super.hashCode(), insertSql, querySql, setupSql, rootQuerySql, insertStatement, queryStatement);
    }

    /**
     * @since 0.2.0
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("insertSql", insertSql)
                          .add("querySql", querySql)
                          .add("setupSql", setupSql)
                          .add("rootQuerySql", rootQuerySql)
                          .add("insertStatement", insertStatement)
                          .add("queryStatement", queryStatement)
                          .toString();
    }
}
