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

package cf.kayon.core.adjective.impl;

import cf.kayon.core.Count;
import cf.kayon.core.adjective.AdjectiveForm;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static cf.kayon.core.adjective.impl.AdjectiveDeclensionTestingUtil.assertStandardEquals;
import static cf.kayon.core.adjective.impl.AdjectiveDeclensionTestingUtil.assertVocativeEquals;

public class OAAdjectiveDeclensionTest
{

    private static final Logger LOGGER = LoggerFactory.getLogger(OAAdjectiveDeclension.class);

    @Test
    public void testCorrectDeclining() throws Exception
    {
        AdjectiveDeclensionTestingUtil.testCorrectDeclining(
                OAAdjectiveDeclension.getInstance(), "alb",
                // Positive
                "albus", "albī", "albō", "album", "albō", "albe",
                "alba", "albae", "albae", "albam", "albā", "alba",
                "album", "albī", "albō", "album", "albō", "album",

                "albī", "albōrum", "albīs", "albōs", "albīs", "albī",
                "albae", "albārum", "albīs", "albās", "albīs", "albae",
                "alba", "albōrum", "albīs", "alba", "albīs", "alba",

                // Comparative
                "albior", "albioris", "albiorī", "albiorem", "albiore", "albior",
                "albior", "albioris", "albiorī", "albiorem", "albiore", "albior",
                "albius", "albioris", "albiorī", "albius", "albiore", "albius",

                "albiorēs", "albiorum", "albioribus", "albiorēs", "albioribus", "albiorēs",
                "albiorēs", "albiorum", "albioribus", "albiorēs", "albioribus", "albiorēs",
                "albiora", "albiorum", "albioribus", "albiora", "albioribus", "albiora",

                // Superlative
                "albissimus", "albissimī", "albissimō", "albissimum", "albissimō", "albissime",
                "albissima", "albissimae", "albissimae", "albissimam", "albissimā", "albissima",
                "albissimum", "albissimī", "albissimō", "albissimum", "albissimō", "albissimum",

                "albissimī", "albissimōrum", "albissimīs", "albissimōs", "albissimīs", "albissimī",
                "albissimae", "albissimārum", "albissimīs", "albissimās", "albissimīs", "albissimae",
                "albissima", "albissimōrum", "albissimīs", "albissima", "albissimīs", "albissima");
    }

    @Test
    public void testEqualForms()
    {
        for (AdjectiveForm adjectiveForm : AdjectiveForm.values())
        {
            Set<AdjectiveForm> equalForms = OAAdjectiveDeclension.getInstance().getEqualForms(adjectiveForm);
            if (adjectiveForm.getCount() == Count.PLURAL)
                assertVocativeEquals(adjectiveForm, equalForms);
            else
                assertStandardEquals(adjectiveForm, equalForms);
        }
    }
}
