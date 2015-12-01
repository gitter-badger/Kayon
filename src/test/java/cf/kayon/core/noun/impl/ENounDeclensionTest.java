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

import static org.junit.Assert.*;

public class ENounDeclensionTest
{
    @Test
    public void testENounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(
                ENounDeclension.getInstance(), Gender.FEMININE, "r",
                "rēs", "reī", "reī", "rem", "rē", "rēs",
                "rēs", "rērum", "rēbus", "rēs", "rēbus", "rēs");

        NounDeclensionTestingUtil.testCorrectDeclining(
                ENounDeclension.getInstance(), Gender.MASCULINE, "di",
                "diēs", "diēī", "diēī", "diem", "diē", "diēs",
                "diēs", "diērum", "diēbus", "diēs", "diēbus", "diēs");

        assertTrue(ENounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ENounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertFalse(ENounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.FEMININE, ENounDeclension.getInstance().getPrimaryGender());
    }
}
