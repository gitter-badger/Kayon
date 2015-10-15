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

public class INounDeclensionTest
{
    @Test
    public void testINounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(INounDeclension.getInstance(), Gender.FEMININE, "dent",
                                                       new ImmutablePair<>("dēns", false),
                                                       new ImmutablePair<>("dentis", true),
                                                       new ImmutablePair<>("dentī", true),
                                                       new ImmutablePair<>("dentem", true),
                                                       new ImmutablePair<>("dente", true),
                                                       new ImmutablePair<>("dēns", false),

                                                       new ImmutablePair<>("dentēs", true),
                                                       new ImmutablePair<>("dentium", true),
                                                       new ImmutablePair<>("dentibus", true),
                                                       new ImmutablePair<>("dentēs", true),
                                                       new ImmutablePair<>("dentibus", true),
                                                       new ImmutablePair<>("dentēs", true)
        );

        NounDeclensionTestingUtil.testCorrectDeclining(INounDeclension.getInstance(), Gender.NEUTER, "mar",
                                                       new ImmutablePair<>("mare", false),
                                                       new ImmutablePair<>("maris", true),
                                                       new ImmutablePair<>("marī", true),
                                                       new ImmutablePair<>("mare", false),
                                                       new ImmutablePair<>("marī", true),
                                                       new ImmutablePair<>("mare", false),

                                                       new ImmutablePair<>("maria", true),
                                                       new ImmutablePair<>("marium", true),
                                                       new ImmutablePair<>("maribus", true),
                                                       new ImmutablePair<>("maria", true),
                                                       new ImmutablePair<>("maribus", true),
                                                       new ImmutablePair<>("maria", true)
        );

        assertTrue(INounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(INounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(INounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.FEMININE, INounDeclension.getInstance().getPrimaryGender());
    }
}
