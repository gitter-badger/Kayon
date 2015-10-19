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
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounDeclensionUtil;
import cf.kayon.core.util.KayonReference;
import cf.kayon.core.util.StringUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NounSQLFactory
{
    public static final String SQL_INSERT = "INSERT INTO NOUNS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String SQL_SINGLE_RECONSTRUCT = "SELECT * FROM NOUNS WHERE rootWord = ?";

    public static final String SQL_QUERY = "SELECT * FROM NOUNS WHERE nomSg = ? OR genSg = ? OR datSg = ? OR accSg = ? OR ablSg = ? OR vocSg = ? OR " +
                                           "nomPl = ? OR genPl = ? OR datPl = ? OR accPl = ? OR ablPl = ? OR vocPl = ?";

    public static final String SQL_SETUP = "CREATE TABLE IF NOT EXISTS NOUNS (" +
                                           "rootWord TEXT PRIMARY KEY NOT NULL ON CONFLICT REPLACE," +
                                           "gender INTEGER NOT NULL," +
                                           "nounDeclension TEXT," +
                                           "translationsJson TEXT NOT NULL," +
                                           "nomSg TEXT, genSg TEXT, datSg TEXT, accSg TEXT, ablSg TEXT, vocSg TEXT," +
                                           "nomPl TEXT, genPl TEXT, datPl TEXT, accPl TEXT, ablPl TEXT, vocPl TEXT," +
                                           "nomSgDef TEXT, genSgDef TEXT, datSgDef TEXT, accSgDef TEXT, ablSgDef TEXT, vocSgDef TEXT," +
                                           "nomPlDef TEXT, genPlDef TEXT, datPlDef TEXT, accPlDef TEXT, ablPlDef TEXT, vocPlDef TEXT);";

    private static final Map<Connection, SQLBuffer> map = Maps.newHashMap();

    private static PreparedStatement doBuffer(Connection connection, SQLQueryType type) throws SQLException
    {
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

    public static void saveNounToDatabase(Connection connection, Noun noun) throws SQLException
    {
        PreparedStatement insertStatement = doBuffer(connection, SQLQueryType.SQL_INSERT);
        insertStatement.setString(1, noun.getRootWord()); // THIS SHOULD REMAIN SPECIAL
        insertStatement.setInt(2, SQLUtil.idForGender(noun.getGender()));
        if (noun.getNounDeclension() != null)
            insertStatement.setString(3, noun.getNounDeclension().getClass().getName());
        else
            insertStatement.setString(3, null);
        insertStatement.setString(4, KayonReference.getGson().toJson(noun.getTranslations()));
        int counter = 5;
        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                @Nullable
                String formOrNull = noun.getForm(caze, count);
                if (formOrNull != null)
                    formOrNull = StringUtil.unSpecialString(formOrNull);
                @Nullable
                String definedForm = noun.getDefinedForm(caze, count);
                insertStatement.setString(counter + 12, definedForm);
                insertStatement.setString(counter++, formOrNull); // unSpecialed insert, could be null
            }
        insertStatement.executeUpdate();
    }

    @Nullable
    public static Noun constructNounFromDatabase(Connection connection, String rootWordToSearchFor) throws SQLException
    {
        PreparedStatement singleReconstructStatement = doBuffer(connection, SQLQueryType.SQL_SINGLE_RECONSTRUCT);
        singleReconstructStatement.setString(1, rootWordToSearchFor);
        try (ResultSet results = singleReconstructStatement.executeQuery())
        {
            if (!results.next())
                return null;

            return constructNounFromResultSet(results);
        }
    }

    // It is the task of the caller to close resources
    @Nullable
    @SuppressWarnings("unchecked")
    public static Noun constructNounFromResultSet(ResultSet resultSet) throws SQLException
    {
        @NotNull
        String rootWord = resultSet.getString(1);
        @NotNull
        Gender gender = SQLUtil.genderForId(resultSet.getInt(2));
        NounDeclension nounDeclension = NounDeclensionUtil.forName(resultSet.getString(3));
        Map<String, String> translations = KayonReference.getGson().fromJson(resultSet.getString(4), Map.class);
        Noun noun = new Noun(nounDeclension, gender, rootWord);
        noun.setTranslations(translations);
        int counter = 17;
        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                @Nullable
                String formOrNull = resultSet.getString(counter);
                if (formOrNull != null)
                    try
                    {
                        noun.setDefinedForm(caze, count, formOrNull);
                    } catch (PropertyVetoException ignored) {} // Empty value in cell, leave defined form as null
                counter++;
            }
        return noun;
    }

    @NotNull
    public static Set<Noun> queryNouns(Connection connection, String formToSearch) throws SQLException
    {
        return queryNounsFromUnSpecialForm(connection, StringUtil.unSpecialString(formToSearch));
    }

    @NotNull
    private static Set<Noun> queryNounsFromUnSpecialForm(Connection connection, String unSpecialString) throws SQLException
    {
        HashSet<Noun> set = Sets.newHashSet();
        PreparedStatement queryStatement = doBuffer(connection, SQLQueryType.SQL_QUERY);
        for (int i = 1; i <= 12; i++)
            queryStatement.setString(i, unSpecialString);
        try (ResultSet results = queryStatement.executeQuery())
        {
            while (results.next())
                set.add(constructNounFromResultSet(results));
        }
        return set;
    }

    public static void setupDatabaseForNouns(Connection connection) throws SQLException
    {
        try (PreparedStatement setupStatement = connection.prepareStatement(SQL_SETUP))
        {
            setupStatement.executeUpdate();
        }
    }

    private enum SQLQueryType
    {
        SQL_INSERT, SQL_SINGLE_RECONSTRUCT, SQL_QUERY
    }

    private static class SQLBuffer
    {
        private final PreparedStatement SQL_INSERT;
        private final PreparedStatement SQL_SINGLE_RECONSTRUCT;
        private final PreparedStatement SQL_QUERY;

        public SQLBuffer(PreparedStatement SQL_INSERT, PreparedStatement SQL_SINGLE_RECONSTRUCT, PreparedStatement SQL_QUERY)
        {
            this.SQL_INSERT = SQL_INSERT;
            this.SQL_SINGLE_RECONSTRUCT = SQL_SINGLE_RECONSTRUCT;
            this.SQL_QUERY = SQL_QUERY;
        }

        public PreparedStatement getFor(SQLQueryType queryType)
        {
            switch (queryType)
            {
                case SQL_INSERT:
                    return SQL_INSERT;
                case SQL_SINGLE_RECONSTRUCT:
                    return SQL_SINGLE_RECONSTRUCT;
                case SQL_QUERY:
                    return SQL_QUERY;
                default:
                    throw new Error("Class version mismatch!");
            }
        }
    }
}
