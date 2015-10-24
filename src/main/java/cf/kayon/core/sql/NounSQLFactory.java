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

import cf.kayon.core.Case;
import cf.kayon.core.CaseHandling;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounDeclensionUtil;
import cf.kayon.core.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Used to perform database actions with nouns.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class NounSQLFactory
{
    /**
     * The SQL string for inserting a {@link Noun} into a H2 database.
     *
     * @since 0.0.1
     */
    public static final String SQL_INSERT = "MERGE INTO NOUNS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * The SQL string for querying a {@link Noun} by its {@link UUID} from a H2 database.
     *
     * @since 0.0.1
     */
    public static final String SQL_SINGLE_RECONSTRUCT = "SELECT * FROM NOUNS WHERE UUID = ?;";

    /**
     * The SQL string for querying {@link Noun}s by a finite form from a H2 database.
     *
     * @since 0.0.1
     */
    public static final String SQL_QUERY = "SELECT * FROM NOUNS WHERE " +
                                           "CONCAT_WS('|', `NOMSG`, `GENSG`, `DATSG`, `ACCSG`, `ABLSG`, `VOCSG`, `NOMPL`, `GENPL`, `DATPL`, `ACCPL`, `ABLPL`, `VOCPL`, " +
                                           "`NOMSGDEF`, `GENSGDEF`, `DATSGDEF`, `ACCSGDEF`, `ABLSGDEF`, `VOCSGDEF`, `NOMPLDEF`, `GENPLDEF`, `DATPLDEF`, `ACCPLDEF`, `ABLPLDEF`, `VOCPLDEF`) " +
                                           "REGEXP ?;"; // Thanks @Buttle Butkus http://stackoverflow.com/a/20834505/4464702

    /**
     * The SQL string for setting up a H2 database for noun operations.
     *
     * @since 0.0.1
     */
    public static final String SQL_SETUP = "CREATE TABLE IF NOT EXISTS NOUNS (" +
                                           "ROOTWORD VARCHAR NOT NULL, " +
                                           "UUID UUID PRIMARY KEY, " +
                                           "GENDER TINYINT NOT NULL, " +
                                           "NOUNDECLENSION VARCHAR, " +
                                           "TRANSLATIONS OTHER NOT NULL, " +
                                           "NOMSG VARCHAR, GENSG VARCHAR, DATSG VARCHAR, ACCSG VARCHAR, ABLSG VARCHAR, VOCSG VARCHAR, " +
                                           "NOMPL VARCHAR, GENPL VARCHAR, DATPL VARCHAR, ACCPL VARCHAR, ABLPL VARCHAR, VOCPL VARCHAR, " +
                                           "NOMSGDEF VARCHAR, GENSGDEF VARCHAR, DATSGDEF VARCHAR, ACCSGDEF VARCHAR, ABLSGDEF VARCHAR, VOCSGDEF VARCHAR, " +
                                           "NOMPLDEF VARCHAR, GENPLDEF VARCHAR, DATPLDEF VARCHAR, ACCPLDEF VARCHAR, ABLPLDEF VARCHAR, VOCPLDEF VARCHAR);";

    /**
     * The buffer map. See {@link #doBuffer(Connection, SQLQueryType)} for more context.
     *
     * @see #doBuffer(Connection, SQLQueryType)
     * @since 0.0.1
     */
    @NotNull
    private static final Map<Connection, SQLBuffer> map = Maps.newHashMap(); // Prevents recompiling of PreparedStatements every time

    /**
     * This method is responsible for buffering {@link PreparedStatement}s for all {@code Connection}s this class had ever to deal with.
     * This exists to prevent recompilation of SQL syntax every time a method is executed.
     * <p>
     * All {@link PreparedStatement}s are {@link #closeAll() being closed} on VM shutdown {@link Runtime#addShutdownHook(Thread) automatically}.
     * <p>
     * One may always clear the buffer (all {@link PreparedStatement}s are being closed in the same process) by invoking {@link #clearBuffer()}.
     *
     * @param connection The connection to which to retreieve a buffered {@link PreparedStatement}.
     * @param type       The type of SQL query.
     * @return A {@link PreparedStatement}. Never {@code null}.
     * @throws SQLException If there was an error when preparing the statements for the buffer.
     * @see cf.kayon.core.sql.NounSQLFactory.SQLQueryType
     * @see cf.kayon.core.sql.NounSQLFactory.SQLBuffer
     * @since 0.0.1
     */
    @NotNull
    private static PreparedStatement doBuffer(Connection connection, SQLQueryType type) throws SQLException
    {
        checkNotNull(connection);
        SQLBuffer buf = map.get(connection);
        if (buf == null)
        {
            buf = new SQLBuffer(connection.prepareStatement(SQL_INSERT),
                                connection.prepareStatement(SQL_SINGLE_RECONSTRUCT),
                                connection.prepareStatement(SQL_QUERY));
            map.put(connection, buf);
        }
        return buf.getFor(type);
    }

    /**
     * Clears the buffer created by {@link #doBuffer(Connection, SQLQueryType)}.
     * <p>
     * All buffered {@link PreparedStatement}s are being closed and afterwards removed from the buffer.
     *
     * @see #doBuffer(Connection, SQLQueryType)
     * @since 0.0.1
     */
    public static void clearBuffer()
    {
        closeAll();
        map.clear();
    }

    /**
     * Closes all {@link PreparedStatement}s in the buffer.
     *
     * @see #doBuffer(Connection, SQLQueryType)
     * @since 0.0.1
     */
    private static void closeAll()
    {
        map.forEach((connection, sqlBuffer) -> {
            for (SQLQueryType sqlQueryType : SQLQueryType.values())
            {
                PreparedStatement statement = sqlBuffer.getFor(sqlQueryType);
                try
                {
                    statement.close();
                } catch (SQLException ignored) {}
            }
        });
    }

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(NounSQLFactory::closeAll, "NounSQLFactory-closer"));
    }

    /**
     * Saves a noun to the database.
     * <p>
     * If the specified noun did not have a UUID before, it gets a random UUID assigned.
     * <p>
     * Note: This method is {@link #doBuffer(Connection, SQLQueryType) buffered}.
     *
     * @param connection The database connection to save to.
     * @param noun       The noun to save.
     * @throws SQLException If there are any issues when executing the SQL update against the database connection.
     * @since 0.0.1
     */
    public static void saveNounToDatabase(Connection connection, Noun noun) throws SQLException
    {
        PreparedStatement insertStatement = doBuffer(connection, SQLQueryType.SQL_INSERT);
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
        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                @Nullable
                String formOrNull = noun.getForm(caze, count);
                @Nullable
                String definedForm = noun.getDefinedForm(caze, count);

                insertStatement.setString(counter + 12, definedForm);
                insertStatement.setString(counter++, formOrNull);
            }
        insertStatement.executeUpdate();
    }

    /**
     * Reconstructs a noun with the given {@link UUID} from the database.
     * <p>
     * Note: This method is {@link #doBuffer(Connection, SQLQueryType) buffered}.
     *
     * @param connection   The connection to the database.
     * @param uuidToSelect The UUID of the noun to reconstruct.
     * @return A {@link Noun} (as specified by {@link #constructNounFromResultSet(ResultSet)}. {@code null} if no such noun could be found.
     * @throws SQLException         If there were any errors executing the SQL query against the connection.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @Nullable
    public static Noun constructNounFromDatabase(@NotNull Connection connection, @NotNull UUID uuidToSelect) throws SQLException
    {
        checkNotNull(uuidToSelect);
        PreparedStatement singleReconstructStatement = doBuffer(connection, SQLQueryType.SQL_SINGLE_RECONSTRUCT);
        singleReconstructStatement.setObject(1, uuidToSelect);
        try (ResultSet results = singleReconstructStatement.executeQuery())
        {
            if (!results.next())
                return null;

            return constructNounFromResultSet(results);
        }
    }

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
    public static Noun constructNounFromResultSet(@NotNull ResultSet resultSet) throws SQLException
    {
        @NotNull
        String rootWord = resultSet.getString(1);
        @NotNull
        UUID uuid = (UUID) resultSet.getObject(2);
        @NotNull
        Gender gender = SQLUtil.genderForId(resultSet.getByte(3));
        NounDeclension nounDeclension = NounDeclensionUtil.forName(resultSet.getString(4));
        Map<String, String> translations = (Map<String, String>) resultSet.getObject(5);
        Noun noun = new Noun(nounDeclension, gender, rootWord);
        noun.setTranslations(translations);
        noun.initializeUuid(uuid);
        int counter = 18;
        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                @Nullable
                String formOrNull = resultSet.getString(counter++);
                try
                {
                    noun.setDefinedForm(caze, count, formOrNull);
                } catch (PropertyVetoException ignored) {} // Empty value in cell, leave defined form as null
            }
        return noun;
    }

    /**
     * Gets a {@link Set} of {@link Noun}s out of a database connection by the specified form.
     * Searches in the table {@code NOUNS}.
     * <p>
     * Note: This method is {@link #doBuffer(Connection, SQLQueryType) buffered}.
     *
     * @param connection   The connection to use.
     * @param formToSearch The form to search. May be any kind of special form. Should not contain uppercase characters.
     * @return A {@link Set} of {@link Noun}s. May be empty if no nouns have been found.
     * @throws SQLException         If a error in executing the query against the database connection occurs.
     * @throws NullPointerException If {@code connection} or {@code formToSearch} is {@code null}.
     * @since 0.0.1
     */
    @NotNull
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public static List<Noun> queryNouns(@NotNull Connection connection, @NotNull String formToSearch) throws SQLException
    {
        return queryNounsFromRegex(connection, StringUtil.anySpecialRegex(formToSearch));
    }

    /**
     * Gets a {@link Set} of {@link Noun}s out of a database connection by the specified form.
     * Searches in the table {@code NOUNS}.
     * <p>
     * Note: This method is {@link #doBuffer(Connection, SQLQueryType) buffered}.
     *
     * @param connection The connection to use.
     * @param regex      The form's regular expression as returned by {@link StringUtil#anySpecialRegex(String)}.
     * @return A {@link Set} of {@link Noun}s. May be empty if no nouns have been found.
     * @throws SQLException         If a error in executing the query occurs.
     * @throws NullPointerException If {@code connection} or {@code regex} is {@code null}.
     * @since 0.0.1
     */
    @NotNull
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private static List<Noun> queryNounsFromRegex(@NotNull Connection connection, @NotNull String regex) throws SQLException
    {
        checkNotNull(regex);
        PreparedStatement queryStatement = doBuffer(connection, SQLQueryType.SQL_QUERY);
        ArrayList<Noun> list = Lists.newArrayList();
        queryStatement.setString(1, regex);
        try (ResultSet results = queryStatement.executeQuery())
        {
            while (results.next())
                list.add(constructNounFromResultSet(results));
        }
        return list;
    }


    /**
     * Makes sure that the {@code NOUNS} table exists in the specified connection to a database.
     * <p>
     * If the {@code NOUNS} table already exists, nothing is changed.
     * <p>
     * Note: No {@link #doBuffer(Connection, SQLQueryType) buffering} is performed, since a) this method does not use a {@link PreparedStatement} and
     * b) this method is expected to be executed against a connection only once.
     *
     * @param connection The connection to the database.
     * @throws SQLException         If there were any errors when executing the SQL statements.
     * @throws NullPointerException If the {@code connection} is null.
     * @since 0.0.1
     */
    public static void setupDatabaseForNouns(@NotNull Connection connection) throws SQLException
    {
        try (Statement statement = connection.createStatement())
        {
            statement.execute(SQL_SETUP);
        }
    }

    /**
     * Defines one of the possible SQL actions to be performed.
     *
     * @author Ruben Anders
     * @see NounSQLFactory#doBuffer(Connection, SQLQueryType)
     * @see cf.kayon.core.sql.NounSQLFactory.SQLBuffer
     * @since 0.0.1
     */
    private enum SQLQueryType
    {
        /**
         * Represents the SQL statement for inserting a new noun or replacing a existing noun with the same {@link UUID}.
         *
         * @since 0.0.1
         */
        SQL_INSERT,
        /**
         * Represents the SQL statement for retrieving a noun by its {@link UUID}.
         *
         * @since 0.0.1
         */
        SQL_SINGLE_RECONSTRUCT,
        /**
         * Represents the SQL statement for querying nouns by a specified regular expression (to match any special forms of the noun).
         *
         * @see StringUtil#anySpecialRegex(String)
         * @see NounSQLFactory#queryNouns(Connection, String)
         * @see NounSQLFactory#queryNounsFromRegex(Connection, String)
         * @since 0.0.1
         */
        SQL_QUERY
    }

    /**
     * Buffers all SQL queries for a single connection.
     *
     * @author Ruben Anders
     * @see NounSQLFactory#doBuffer(Connection, SQLQueryType)
     * @see cf.kayon.core.sql.NounSQLFactory.SQLQueryType
     * @since 0.0.1
     */
    private static class SQLBuffer
    {
        /**
         * The {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType#SQL_INSERT SQL_INSERT} statement.
         *
         * @since 0.0.1
         */
        @NotNull
        private final PreparedStatement SQL_INSERT;

        /**
         * The {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType#SQL_SINGLE_RECONSTRUCT SQL_SINGLE_RECONSTRUCT} statement.
         *
         * @since 0.0.1
         */
        @NotNull
        private final PreparedStatement SQL_SINGLE_RECONSTRUCT;

        /**
         * The {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType#SQL_QUERY SQL_QUERY} statement.
         *
         * @since 0.0.1
         */
        @NotNull
        private final PreparedStatement SQL_QUERY;

        /**
         * Constructs a new SQLBuffer;
         *
         * @param SQL_INSERT             The {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType#SQL_INSERT SQL_INSERT} {@link PreparedStatement}.
         * @param SQL_SINGLE_RECONSTRUCT The {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType#SQL_SINGLE_RECONSTRUCT SQL_SINGLE_RECONSTRUCT} {@link PreparedStatement}.
         * @param SQL_QUERY              The {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType#SQL_QUERY SQL_QUERY} {@link PreparedStatement}.
         * @throws NullPointerException If any of the arguments is {@code null}.
         * @since 0.0.1
         */
        public SQLBuffer(@NotNull PreparedStatement SQL_INSERT, @NotNull PreparedStatement SQL_SINGLE_RECONSTRUCT, @NotNull PreparedStatement SQL_QUERY)
        {
            checkNotNull(SQL_INSERT);
            checkNotNull(SQL_SINGLE_RECONSTRUCT);
            checkNotNull(SQL_QUERY);
            this.SQL_INSERT = SQL_INSERT;
            this.SQL_SINGLE_RECONSTRUCT = SQL_SINGLE_RECONSTRUCT;
            this.SQL_QUERY = SQL_QUERY;
        }

        /**
         * Returns a {@link PreparedStatement} for a {@link cf.kayon.core.sql.NounSQLFactory.SQLQueryType}.
         *
         * @param queryType The query type.
         * @return A {@link PreparedStatement}. Never {@code null}.
         * @throws NullPointerException If {@code queryType} is {@code null}.
         * @throws Error                If the specified enum value is unknown (Possible if class versions mismatch).
         * @since 0.0.1
         */
        @NotNull
        public PreparedStatement getFor(@NotNull SQLQueryType queryType)
        {
            checkNotNull(queryType);
            switch (queryType)
            {
                case SQL_INSERT:
                    return SQL_INSERT;
                case SQL_SINGLE_RECONSTRUCT:
                    return SQL_SINGLE_RECONSTRUCT;
                case SQL_QUERY:
                    return SQL_QUERY;
                default:
                    throw new Error("Invalid enum value");
            }
        }
    }
}
