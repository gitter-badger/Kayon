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

public class MixedNounDeclensionTest
{

    @Test
    public void testMixedNounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(MixedNounDeclension.getInstance(), Gender.FEMININE, "urb",
                                                       new ImmutablePair<>("urbs", false),
                                                       new ImmutablePair<>("urbis", true),
                                                       new ImmutablePair<>("urbi", true),
                                                       new ImmutablePair<>("urbem", true),
                                                       new ImmutablePair<>("urbe", true),
                                                       new ImmutablePair<>("urbs", false),

                                                       new ImmutablePair<>("urbēs", true),
                                                       new ImmutablePair<>("urbium", true),
                                                       new ImmutablePair<>("urbibus", true),
                                                       new ImmutablePair<>("urbēs", true),
                                                       new ImmutablePair<>("urbibus", true),
                                                       new ImmutablePair<>("urbēs", true)
        );
        
        NounDeclensionTestingUtil.testCorrectDeclining(MixedNounDeclension.getInstance(), Gender.FEMININE, "noct",
                                                       new ImmutablePair<>("nox", false),
                                                       new ImmutablePair<>("noctis", true),
                                                       new ImmutablePair<>("noctī", true),
                                                       new ImmutablePair<>("noctem", true),
                                                       new ImmutablePair<>("nocte", true),
                                                       new ImmutablePair<>("nox", false),

                                                       new ImmutablePair<>("noctēs", true),
                                                       new ImmutablePair<>("noctium", true),
                                                       new ImmutablePair<>("noctibus", true),
                                                       new ImmutablePair<>("noctēs", true),
                                                       new ImmutablePair<>("noctibus", true),
                                                       new ImmutablePair<>("noctēs", true)
        );

        assertTrue(MixedNounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(MixedNounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertFalse(MixedNounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.FEMININE, MixedNounDeclension.getInstance().getPrimaryGender());
    }
}
