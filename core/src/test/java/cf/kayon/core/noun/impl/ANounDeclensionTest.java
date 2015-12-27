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

import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import org.junit.Test;

import static org.junit.Assert.*;

public class ANounDeclensionTest
{

    @Test
    public void testANounDeclension() throws FormingException
    {
        NounDeclensionTestingUtil.testCorrectDeclining(
                ANounDeclension.getInstance(), Gender.FEMININE, "ancill",
                "ancilla", "ancillae", "ancillae", "ancillam", "ancillā", "ancilla",
                "ancillae", "ancillārum", "ancillīs", "ancillās", "ancillīs", "ancillae"
        );

        NounDeclensionTestingUtil.testCorrectDeclining(
                ANounDeclension.getInstance(), Gender.MASCULINE, "conviv",
                "conviva", "convivae", "convivae", "convivam", "convivā", "conviva",
                "convivae", "convivārum", "convivīs", "convivās", "convivīs", "convivae"
        );

        assertTrue(ANounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ANounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertFalse(ANounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.FEMININE, ANounDeclension.getInstance().getPrimaryGender());
    }

}
