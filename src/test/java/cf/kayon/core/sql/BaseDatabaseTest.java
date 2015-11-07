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
import cf.kayon.core.noun.NounForm;
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
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BaseDatabaseTest
{

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    final List<Noun> examples = Lists.newArrayList();
    private Connection connection;

    @Before
    public void setUp() throws SQLException, PropertyVetoException
    {
        examples.add(new Noun(ANounDeclension.getInstance(), Gender.FEMININE, "ancill"));  // ancilla
        examples.add(new Noun(ANounDeclension.getInstance(), Gender.FEMININE, "silv"));    // serva
        examples.add(new Noun(ANounDeclension.getInstance(), Gender.MASCULINE, "conviv")); // conviva

        Noun defExample = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "serv"); // servus
        defExample.setDefinedForm(NounForm.of(Case.DATIVE, Count.SINGULAR), "DatSgDef");
        defExample.setDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL), "banana");
        defExample.setDefinedForm(NounForm.of(Case.ACCUSATIVE, Count.SINGULAR), "jetbrains");
        examples.add(defExample);

        Noun translationExample = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin"); // dominus
        translationExample.getTranslations().put(new Locale("de"), "(Haus-) Herr");
        translationExample.getTranslations().put(new Locale("en"), "owner of a residence, a lord");
        examples.add(translationExample);

        Noun defAndTranslationExample = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "mur"); // murus
        defAndTranslationExample.setDefinedForm(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), "awudhaowudhaowd");
        defAndTranslationExample.setDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL), "aiwjdw");
        defAndTranslationExample.getTranslations().put(new Locale("de"), "Mauer");
        defAndTranslationExample.getTranslations().put(new Locale("en"), "wall");
        defAndTranslationExample.getTranslations().put(new Locale("fr"), "mur");
        examples.add(defAndTranslationExample);

        Noun noDeclensionExample = new Noun(Gender.MASCULINE, "abc123def");
        for (NounForm nounForm : NounForm.values())
            noDeclensionExample.setDefinedForm(nounForm, nounForm.getCount() + "-āēīōū-" + nounForm.getCase()); // Unicode test
        examples.add(noDeclensionExample);

        connection = DriverManager.getConnection("jdbc:h2:./database-nouns-small");
        NounSQLFactory.setupDatabaseForNouns(connection);
    }

    @Test
    public void testFactory() throws SQLException
    {
        for (Noun current : examples)
            NounSQLFactory.saveNounToDatabase(connection, current);

        int iterations;
        try (ResultSet results = connection.createStatement().executeQuery("SELECT * FROM NOUNS;"))
        {
            iterations = 0;
            while (results.next())
            {
                Noun exampleTemplate = examples.get(iterations++);
                Noun reconstructed = NounSQLFactory.constructNounFromResultSet(results);
                assertNotNull(reconstructed);
                assertEquals(exampleTemplate, reconstructed);
            }
        }
        assertEquals(examples.size(), iterations);
    }

    @After
    public void closeDatabase() throws SQLException
    {
        connection.close();
    }
}
