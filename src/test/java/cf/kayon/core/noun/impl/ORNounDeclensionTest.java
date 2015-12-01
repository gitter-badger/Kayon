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

public class ORNounDeclensionTest
{
    @Test
    public void testORNounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(ORNounDeclension.getInstance(), Gender.MASCULINE, "puer",
                                                       "puer", false, "puerī", "puerō", "puerum", "puerō", "puer", false,
                                                       "puerī", "puerōrum", "puerīs", "puerōs", "puerīs", "puerī");

        assertTrue(ORNounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ORNounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertFalse(ORNounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.MASCULINE, ORNounDeclension.getInstance().getPrimaryGender());
    }
}
