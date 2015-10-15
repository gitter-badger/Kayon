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

package cf.kayon.core.noun.impl;

import cf.kayon.core.Gender;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ConsonantNounDeclensionTest
{

    @Test
    public void testConsonantNounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(ConsonantNounDeclension.getInstance(), Gender.FEMININE, "victor",
                                                       new ImmutablePair<>("victor", false),
                                                       new ImmutablePair<>("victoris", true),
                                                       new ImmutablePair<>("victorī", true),
                                                       new ImmutablePair<>("victorem", true),
                                                       new ImmutablePair<>("victore", true),
                                                       new ImmutablePair<>("victor", false),

                                                       new ImmutablePair<>("victorēs", true),
                                                       new ImmutablePair<>("victorum", true),
                                                       new ImmutablePair<>("victoribus", true),
                                                       new ImmutablePair<>("victorēs", true),
                                                       new ImmutablePair<>("victoribus", true),
                                                       new ImmutablePair<>("victorēs", true)
        );

        NounDeclensionTestingUtil.testCorrectDeclining(ConsonantNounDeclension.getInstance(), Gender.MASCULINE, "milit",
                                                       new ImmutablePair<>("miles", false),
                                                       new ImmutablePair<>("militis", true),
                                                       new ImmutablePair<>("militī", true),
                                                       new ImmutablePair<>("militem", true),
                                                       new ImmutablePair<>("milite", true),
                                                       new ImmutablePair<>("miles", false),

                                                       new ImmutablePair<>("militēs", true),
                                                       new ImmutablePair<>("militum", true),
                                                       new ImmutablePair<>("militibus", true),
                                                       new ImmutablePair<>("militēs", true),
                                                       new ImmutablePair<>("militibus", true),
                                                       new ImmutablePair<>("militēs", true)
        );

        NounDeclensionTestingUtil.testCorrectDeclining(ConsonantNounDeclension.getInstance(), Gender.FEMININE, "laud",
                                                       new ImmutablePair<>("laus", false),
                                                       new ImmutablePair<>("laudis", true),
                                                       new ImmutablePair<>("laudī", true),
                                                       new ImmutablePair<>("laudem", true),
                                                       new ImmutablePair<>("laude", true),
                                                       new ImmutablePair<>("laus", false),

                                                       new ImmutablePair<>("laudēs", true),
                                                       new ImmutablePair<>("laudum", true),
                                                       new ImmutablePair<>("laudibus", true),
                                                       new ImmutablePair<>("laudēs", true),
                                                       new ImmutablePair<>("laudibus", true),
                                                       new ImmutablePair<>("laudēs", true)
        );

        assertTrue(ConsonantNounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ConsonantNounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(ConsonantNounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.FEMININE, ConsonantNounDeclension.getInstance().getPrimaryGender());
    }
}