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

import cf.kayon.core.*;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class BaseDatabaseTest
{

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseTest.class);
    private final List<Noun> examples = Lists.newArrayList();
    private KayonContext context;

    @Before
    public void setUp() throws SQLException
    {
        context = TestContextUtil.newTestingContext();

        examples.add(new Noun(context, ANounDeclension.getInstance(), Gender.FEMININE, "ancill"));  // ancilla
        examples.add(new Noun(context, ANounDeclension.getInstance(), Gender.FEMININE, "silv"));    // serva
        examples.add(new Noun(context, ANounDeclension.getInstance(), Gender.MASCULINE, "conviv")); // conviva

        Noun defExample = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv"); // servus
        defExample.setDefinedForm(NounForm.of(Case.DATIVE, Count.SINGULAR), "DatSgDef");
        defExample.setDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL), "banana");
        defExample.setDefinedForm(NounForm.of(Case.ACCUSATIVE, Count.SINGULAR), "jetbrains");
        examples.add(defExample);

        Noun translationExample = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "domin"); // dominus
        translationExample.getTranslations().put(new Locale("de"), "(Haus-) Herr");
        translationExample.getTranslations().put(new Locale("en"), "owner of a residence, a lord");
        examples.add(translationExample);

        Noun defAndTranslationExample = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "mur"); // murus
        defAndTranslationExample.setDefinedForm(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), "awudhaowudhaowd");
        defAndTranslationExample.setDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL), "aiwjdw");
        defAndTranslationExample.getTranslations().put(new Locale("de"), "Mauer");
        defAndTranslationExample.getTranslations().put(new Locale("en"), "wall");
        defAndTranslationExample.getTranslations().put(new Locale("fr"), "mur");
        examples.add(defAndTranslationExample);

        Noun noDeclensionExample = new Noun(context, Gender.MASCULINE, "abc123def");
        for (NounForm nounForm : NounForm.values())
            noDeclensionExample.setDefinedForm(nounForm, nounForm.getCount() + "-āēīōū-" + nounForm.getCase()); // Unicode test
        examples.add(noDeclensionExample);
        LOGGER.info("Examples are:");
        examples.forEach(e -> LOGGER.info(e.toString()));
    }

    @Test
    public void testFactory() throws SQLException
    {
        LOGGER.info("Saving nouns to database");
        for (Noun current : examples)
        {
            LOGGER.info("Saving " + current);
            context.getNounSQLFactory().saveNounToDatabase(current);
        }

        int iterations;
        try (ResultSet results = context.getConnection().createStatement().executeQuery("SELECT * FROM NOUNS;"))
        {
            LOGGER.info("Reconstructing nouns.");
            iterations = 0;
            while (results.next())
            {
                Noun exampleTemplate = examples.get(iterations++);
                Noun reconstructed = context.getNounSQLFactory().constructNounFromResultSet(results);
                LOGGER.info("Reconstructed " + reconstructed);
                assertNotNull(reconstructed);
                assertEquals(exampleTemplate, reconstructed);
                assertNotSame(exampleTemplate, reconstructed);
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
