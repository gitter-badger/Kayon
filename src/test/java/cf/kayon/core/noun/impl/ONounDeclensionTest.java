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

public class ONounDeclensionTest
{
    
    @Test
    public void testONounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(ONounDeclension.getInstance(), Gender.MASCULINE, "domin",
                                                       new ImmutablePair<>("dominus", true),
                                                       new ImmutablePair<>("domini", true),
                                                       new ImmutablePair<>("dominō", true),
                                                       new ImmutablePair<>("dominum", true),
                                                       new ImmutablePair<>("dominō", true),
                                                       new ImmutablePair<>("domine", true),

                                                       new ImmutablePair<>("domini", true),
                                                       new ImmutablePair<>("dominōrum", true),
                                                       new ImmutablePair<>("dominīs", true),
                                                       new ImmutablePair<>("dominōs", true),
                                                       new ImmutablePair<>("dominīs", true),
                                                       new ImmutablePair<>("domini", true)
        );

        NounDeclensionTestingUtil.testCorrectDeclining(ONounDeclension.getInstance(), Gender.MASCULINE, "fili",
                                                       new ImmutablePair<>("filius", true),
                                                       new ImmutablePair<>("filiī", true),
                                                       new ImmutablePair<>("filiō", true),
                                                       new ImmutablePair<>("filium", true),
                                                       new ImmutablePair<>("filiō", true),
                                                       new ImmutablePair<>("filī", true), // this is the difference

                                                       new ImmutablePair<>("filiī", true),
                                                       new ImmutablePair<>("filiōrum", true),
                                                       new ImmutablePair<>("filiīs", true),
                                                       new ImmutablePair<>("filiōs", true),
                                                       new ImmutablePair<>("filiīs", true),
                                                       new ImmutablePair<>("filii", true)
        );

        NounDeclensionTestingUtil.testCorrectDeclining(ONounDeclension.getInstance(), Gender.NEUTER, "templ",
                                                       new ImmutablePair<>("templum", true),
                                                       new ImmutablePair<>("templi", true),
                                                       new ImmutablePair<>("templō", true),
                                                       new ImmutablePair<>("templum", true),
                                                       new ImmutablePair<>("templō", true),
                                                       new ImmutablePair<>("templum", true),

                                                       new ImmutablePair<>("templa", true),
                                                       new ImmutablePair<>("templōrum", true),
                                                       new ImmutablePair<>("templīs", true),
                                                       new ImmutablePair<>("templa", true),
                                                       new ImmutablePair<>("templīs", true),
                                                       new ImmutablePair<>("templa", true)
        );

        assertTrue(ONounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ONounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(ONounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.MASCULINE, ONounDeclension.getInstance().getPrimaryGender());
    }
    
}
