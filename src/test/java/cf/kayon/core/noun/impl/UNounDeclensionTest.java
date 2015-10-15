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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class UNounDeclensionTest
{
    
    @Test
    public void testUNounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(UNounDeclension.getInstance(), Gender.MASCULINE, "port",
                                                       new ImmutablePair<>("portus", true),
                                                       new ImmutablePair<>("portūs", true),
                                                       new ImmutablePair<>("portuī", true),
                                                       new ImmutablePair<>("portum", true),
                                                       new ImmutablePair<>("portū", true),
                                                       new ImmutablePair<>("portus", true),

                                                       new ImmutablePair<>("portūs", true),
                                                       new ImmutablePair<>("portuum", true),
                                                       new ImmutablePair<>("portibus", true),
                                                       new ImmutablePair<>("portūs", true),
                                                       new ImmutablePair<>("portibus", true),
                                                       new ImmutablePair<>("portūs", true)
        );
        
        NounDeclensionTestingUtil.testCorrectDeclining(UNounDeclension.getInstance(), Gender.FEMININE, "man",
                                                       new ImmutablePair<>("manus", true),
                                                       new ImmutablePair<>("manūs", true),
                                                       new ImmutablePair<>("manuī", true),
                                                       new ImmutablePair<>("manum", true),
                                                       new ImmutablePair<>("manū", true),
                                                       new ImmutablePair<>("manus", true),

                                                       new ImmutablePair<>("manūs", true),
                                                       new ImmutablePair<>("manuum", true),
                                                       new ImmutablePair<>("manibus", true),
                                                       new ImmutablePair<>("manūs", true),
                                                       new ImmutablePair<>("manibus", true),
                                                       new ImmutablePair<>("manūs", true)
        );

        NounDeclensionTestingUtil.testCorrectDeclining(UNounDeclension.getInstance(), Gender.NEUTER, "corn",
                                                       new ImmutablePair<>("cornū", true),
                                                       new ImmutablePair<>("cornūs", true),
                                                       new ImmutablePair<>("cornū", true),
                                                       new ImmutablePair<>("cornū", true),
                                                       new ImmutablePair<>("cornū", true),
                                                       new ImmutablePair<>("cornū", true),

                                                       new ImmutablePair<>("cornua", true),
                                                       new ImmutablePair<>("cornuum", true),
                                                       new ImmutablePair<>("cornibus", true),
                                                       new ImmutablePair<>("cornua", true),
                                                       new ImmutablePair<>("cornibus", true),
                                                       new ImmutablePair<>("cornua", true)
        );

        assertTrue(UNounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(UNounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(UNounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.MASCULINE, UNounDeclension.getInstance().getPrimaryGender());
    }
}
