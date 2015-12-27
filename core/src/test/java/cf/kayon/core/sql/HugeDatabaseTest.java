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
import cf.kayon.core.KayonContext;
import cf.kayon.core.TestContextUtil;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.impl.ANounDeclension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HugeDatabaseTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HugeDatabaseTest.class);
    private final List<Noun> examples = new ArrayList<>(50);
    private KayonContext context;

    /*
     * Logging, because test takes a long time and it's possible to look at the timing.
     */
    @Before
    public void setUp() throws SQLException, IOException
    {
        context = TestContextUtil.newTestingContext();

        LOGGER.info("Making examples");
        for (int i = 0; i < 50; i++)
        {
            LOGGER.info(i + 1 + "/50");
            examples.add(new Noun(context, ANounDeclension.getInstance(), Gender.FEMININE, Integer.toHexString(i)));
        }
    }

    @Test
    public void testFactory() throws SQLException
    {
        for (Noun current : examples)
        {
            context.getNounSQLFactory().saveNounToDatabase(current);
        }

        int iterations;
        try (ResultSet results = context.getConnection().createStatement().executeQuery("SELECT * FROM NOUNS;"))
        {
            iterations = 0;
            while (results.next())
            {
                Noun exampleTemplate = examples.get(iterations);
                Noun reconstructed = context.getNounSQLFactory().constructNounFromResultSet(results);
                assertNotNull(reconstructed);
                assertEquals(exampleTemplate, reconstructed);
                iterations++;
            }
        }
        assertEquals(examples.size(), iterations);
    }

    @After
    public void closeDatabase() throws SQLException
    {
        TestContextUtil.closeContext(context);
    }
}
