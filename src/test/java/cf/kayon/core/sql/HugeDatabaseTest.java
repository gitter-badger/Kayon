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

import cf.kayon.core.Gender;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.impl.ANounDeclension;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HugeDatabaseTest
{
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    final List<Noun> examples = Lists.newArrayList();
    private Connection connection;

    {
        for (int i = 0; i < 50; i++)
        {
            examples.add(new Noun(ANounDeclension.getInstance(), Gender.FEMININE, Integer.toHexString(i)));
        }
    }

    /*
     * Logging, because test takes a long time and it's possible to look at the timing.
     */
    @Before
    public void setUp() throws SQLException
    {
        LOGGER.info("Connecting...");
        connection = DriverManager.getConnection("jdbc:h2:./database-nouns-huge");
        LOGGER.info("Connected.");

        LOGGER.info("Setting up database for usage...");
        NounSQLFactory.setupDatabaseForNouns(connection);
        LOGGER.info("Finished.");
    }

    @Test
    public void testFactory() throws SQLException
    {
        LOGGER.info("Saving examples to database...");
        for (Noun current : examples)
        {
            LOGGER.info((examples.indexOf(current) + 1) + "/" + examples.size() + "...");
            NounSQLFactory.saveNounToDatabase(connection, current);
        }

        int iterations;
        LOGGER.info("Querying everything from NOUNS...");
        try (ResultSet results = connection.createStatement().executeQuery("SELECT * FROM NOUNS;"))
        {
            LOGGER.info("Done Querying.");
            iterations = 0;
            while (results.next())
            {
                Noun exampleTemplate = examples.get(iterations);
                Noun reconstructed = NounSQLFactory.constructNounFromResultSet(results);
                assertNotNull(reconstructed);
                LOGGER.info("Reconstructed noun with root word " + reconstructed.getRootWord());
                assertEquals(exampleTemplate, reconstructed);
                iterations++;
            }
        }
        assertEquals(examples.size(), iterations);

        LOGGER.info("Full-text searching now.");
        Set<Noun> result = NounSQLFactory.queryNouns(connection, "aa");
        for (Noun noun : result)
        {
            LOGGER.info("Found noun with root word " + noun.getRootWord());
        }
    }

    @After
    public void closeDatabase() throws SQLException
    {
        connection.close();
    }
}
