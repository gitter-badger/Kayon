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
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ConsonantNounDeclensionTest
{

    @Test
    public void testConsonantNounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(
                ConsonantNounDeclension.getInstance(), Gender.FEMININE, "victor",
                "victor", false, "victoris", "victorī", "victorem", "victore", "victor", false,
                "victorēs", "victorum", "victoribus", "victorēs", "victoribus", "victorēs"
        );

        NounDeclensionTestingUtil.testCorrectDeclining(
                ConsonantNounDeclension.getInstance(), Gender.MASCULINE, "milit",
                "miles", false, "militis", "militī", "militem", "milite", "miles", false,
                "militēs", "militum", "militibus", "militēs", "militibus", "militēs");

        NounDeclensionTestingUtil.testCorrectDeclining(
                ConsonantNounDeclension.getInstance(), Gender.FEMININE, "laud",
                "laus", false, "laudis", "laudī", "laudem", "laude", "laus", false,
                "laudēs", "laudum", "laudibus", "laudēs", "laudibus", "laudēs"
        );

        assertTrue(ConsonantNounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ConsonantNounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(ConsonantNounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.FEMININE, ConsonantNounDeclension.getInstance().getPrimaryGender());
    }
}
