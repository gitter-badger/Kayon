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
import cf.kayon.core.noun.impl.ANounDeclension;
import cf.kayon.core.noun.impl.ONounDeclension;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BaseDatabaseTest
{

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    List<Noun> examples = Lists.newArrayList();
    private Connection connection;

    {

    }

    /*
     * Logging, because test takes a long time and it's possible to look at the timing.
     */
    @Before
    public void setUp() throws SQLException, PropertyVetoException
    {
        examples.add(new Noun(ANounDeclension.getInstance(), Gender.FEMININE, "ancill"));  // ancilla
        examples.add(new Noun(ANounDeclension.getInstance(), Gender.FEMININE, "silv"));    // serva
        examples.add(new Noun(ANounDeclension.getInstance(), Gender.MASCULINE, "conviv")); // conviva

        Noun defExample = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "serv"); // servus
        defExample.setDefinedForm(Case.DATIVE, Count.SINGULAR, "DatSgDef");
        defExample.setDefinedForm(Case.GENITIVE, Count.PLURAL, "banana");
        defExample.setDefinedForm(Case.ACCUSATIVE, Count.SINGULAR, "jetbrains");
        examples.add(defExample);

        Noun translationExample = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin"); // dominus
        translationExample.getTranslations().put("de", "(Haus-) Herr");
        translationExample.getTranslations().put("en", "owner of a residence, a lord");
        examples.add(translationExample);

        Noun defAndTranslationExample = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "mur"); // murus
        defAndTranslationExample.setDefinedForm(Case.ACCUSATIVE, Count.PLURAL, "awudhaowudhaowd");
        defAndTranslationExample.setDefinedForm(Case.GENITIVE, Count.PLURAL, "aiwjdw");
        defAndTranslationExample.getTranslations().put("de", "Mauer");
        defAndTranslationExample.getTranslations().put("en", "wall");
        defAndTranslationExample.getTranslations().put("fr", "mur");
        examples.add(defAndTranslationExample);

        Noun noDeclensionExample = new Noun(Gender.MASCULINE, "abc123def");
        for (Count count : Count.values())
            for (Case caze : Case.values())
                noDeclensionExample.setDefinedForm(caze, count, count + "-āēīōū-" + caze); // Unicode test
        examples.add(noDeclensionExample);

        LOGGER.info("Connecting...");
        connection = DriverManager.getConnection("jdbc:sqlite:database-nouns-small.db");
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
            LOGGER.info("Done.");
        }

        int iterations;
        LOGGER.info("Querying everything from NOUNS...");
        try (ResultSet results = connection.createStatement().executeQuery("SELECT * FROM NOUNS;"))
        {
            LOGGER.info("Done Querying.");
            iterations = 0;
            while (results.next())
            {
                LOGGER.info("Reconstructing noun...");
                Noun exampleTemplate = examples.get(iterations);
                Noun reconstructed = NounSQLFactory.constructNounFromResultSet(results);
                assertNotNull(reconstructed);
                LOGGER.info("Reconstructed noun with root word " + reconstructed.getRootWord());
                assertEquals(exampleTemplate, reconstructed);
                iterations++;
            } // results.getRow() seems broken, so instead iterate like this -.-
        }
        assertEquals(examples.size(), iterations);
    }

    @After
    public void closeDatabase() throws SQLException
    {
        connection.close();
    }
}
