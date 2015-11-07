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
import org.apache.commons.lang3.tuple.ImmutablePair;
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
        AdjectiveDeclensionTestingUtil.testCorrectDeclining(OAAdjectiveDeclension.getInstance(), "bon",
                                                            // Positive
                                                            new ImmutablePair<>("bonus", true),
                                                            new ImmutablePair<>("boni", true),
                                                            new ImmutablePair<>("bono", true),
                                                            new ImmutablePair<>("bonum", true),
                                                            new ImmutablePair<>("bono", true),
                                                            new ImmutablePair<>("bone", true),

                                                            new ImmutablePair<>("bona", true),
                                                            new ImmutablePair<>("bonae", true),
                                                            new ImmutablePair<>("bonae", true),
                                                            new ImmutablePair<>("bonam", true),
                                                            new ImmutablePair<>("bona", true),
                                                            new ImmutablePair<>("bona", true),

                                                            new ImmutablePair<>("bonum", true),
                                                            new ImmutablePair<>("boni", true),
                                                            new ImmutablePair<>("bono", true),
                                                            new ImmutablePair<>("bonum", true),
                                                            new ImmutablePair<>("bono", true),
                                                            new ImmutablePair<>("bonum", true),

                                                            new ImmutablePair<>("boni", true),
                                                            new ImmutablePair<>("bonorum", true),
                                                            new ImmutablePair<>("bonis", true),
                                                            new ImmutablePair<>("bonos", true),
                                                            new ImmutablePair<>("bonis", true),
                                                            new ImmutablePair<>("boni", true),

                                                            new ImmutablePair<>("bonae", true),
                                                            new ImmutablePair<>("bonarum", true),
                                                            new ImmutablePair<>("bonis", true),
                                                            new ImmutablePair<>("bonas", true),
                                                            new ImmutablePair<>("bonis", true),
                                                            new ImmutablePair<>("bonae", true),

                                                            new ImmutablePair<>("bona", true),
                                                            new ImmutablePair<>("bonorum", true),
                                                            new ImmutablePair<>("bonis", true),
                                                            new ImmutablePair<>("bona", true),
                                                            new ImmutablePair<>("bonis", true),
                                                            new ImmutablePair<>("bona", true),

                                                            // Comparative
                                                            new ImmutablePair<>("bonior", true),
                                                            new ImmutablePair<>("bonioris", true),
                                                            new ImmutablePair<>("boniori", true),
                                                            new ImmutablePair<>("boniorem", true),
                                                            new ImmutablePair<>("boniore", true),
                                                            new ImmutablePair<>("bonior", true),

                                                            new ImmutablePair<>("bonior", true),
                                                            new ImmutablePair<>("bonioris", true),
                                                            new ImmutablePair<>("boniori", true),
                                                            new ImmutablePair<>("boniorem", true),
                                                            new ImmutablePair<>("boniore", true),
                                                            new ImmutablePair<>("bonior", true),

                                                            new ImmutablePair<>("bonius", true),
                                                            new ImmutablePair<>("bonioris", true),
                                                            new ImmutablePair<>("boniori", true),
                                                            new ImmutablePair<>("bonius", true),
                                                            new ImmutablePair<>("boniore", true),
                                                            new ImmutablePair<>("bonius", true),

                                                            new ImmutablePair<>("boniores", true),
                                                            new ImmutablePair<>("boniorum", true),
                                                            new ImmutablePair<>("bonioribus", true),
                                                            new ImmutablePair<>("boniores", true),
                                                            new ImmutablePair<>("bonioribus", true),
                                                            new ImmutablePair<>("boniores", true),

                                                            new ImmutablePair<>("boniores", true),
                                                            new ImmutablePair<>("boniorum", true),
                                                            new ImmutablePair<>("bonioribus", true),
                                                            new ImmutablePair<>("boniores", true),
                                                            new ImmutablePair<>("bonioribus", true),
                                                            new ImmutablePair<>("boniores", true),

                                                            new ImmutablePair<>("boniora", true),
                                                            new ImmutablePair<>("boniorum", true),
                                                            new ImmutablePair<>("bonioribus", true),
                                                            new ImmutablePair<>("boniora", true),
                                                            new ImmutablePair<>("bonioribus", true),
                                                            new ImmutablePair<>("boniora", true),

                                                            // Superlative
                                                            new ImmutablePair<>("bonissimus", true),
                                                            new ImmutablePair<>("bonissimi", true),
                                                            new ImmutablePair<>("bonissimo", true),
                                                            new ImmutablePair<>("bonissimum", true),
                                                            new ImmutablePair<>("bonissimo", true),
                                                            new ImmutablePair<>("bonissime", true),

                                                            new ImmutablePair<>("bonissima", true),
                                                            new ImmutablePair<>("bonissimae", true),
                                                            new ImmutablePair<>("bonissimae", true),
                                                            new ImmutablePair<>("bonissimam", true),
                                                            new ImmutablePair<>("bonissima", true),
                                                            new ImmutablePair<>("bonissima", true),

                                                            new ImmutablePair<>("bonissimum", true),
                                                            new ImmutablePair<>("bonissimi", true),
                                                            new ImmutablePair<>("bonissimo", true),
                                                            new ImmutablePair<>("bonissimum", true),
                                                            new ImmutablePair<>("bonissimo", true),
                                                            new ImmutablePair<>("bonissimum", true),


                                                            new ImmutablePair<>("bonissimi", true),
                                                            new ImmutablePair<>("bonissimorum", true),
                                                            new ImmutablePair<>("bonissimis", true),
                                                            new ImmutablePair<>("bonissimos", true),
                                                            new ImmutablePair<>("bonissimis", true),
                                                            new ImmutablePair<>("bonissimi", true),


                                                            new ImmutablePair<>("bonissimae", true),
                                                            new ImmutablePair<>("bonissimarum", true),
                                                            new ImmutablePair<>("bonissimis", true),
                                                            new ImmutablePair<>("bonissimas", true),
                                                            new ImmutablePair<>("bonissimis", true),
                                                            new ImmutablePair<>("bonissimae", true),

                                                            new ImmutablePair<>("bonissima", true),
                                                            new ImmutablePair<>("bonissimorum", true),
                                                            new ImmutablePair<>("bonissimis", true),
                                                            new ImmutablePair<>("bonissima", true),
                                                            new ImmutablePair<>("bonissimis", true),
                                                            new ImmutablePair<>("bonissima", true));
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
