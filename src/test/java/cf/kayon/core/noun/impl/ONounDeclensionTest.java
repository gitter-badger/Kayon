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

public class ONounDeclensionTest
{
    
    @Test
    public void testONounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(ONounDeclension.getInstance(), Gender.MASCULINE, "domin",
                                                       "dominus", "dominī", "dominō", "dominum", "dominō", "domine",
                                                       "dominī", "dominōrum", "dominīs", "dominōs", "dominīs", "dominī");

        NounDeclensionTestingUtil.testCorrectDeclining(ONounDeclension.getInstance(), Gender.MASCULINE, "fili",
                                                       "filius", "filiī", "filiō", "filium", "filiō", "filī", // notice: filī is different
                                                       "filiī", "filiōrum", "filiīs", "filiōs", "filiīs", "filiī");

        NounDeclensionTestingUtil.testCorrectDeclining(ONounDeclension.getInstance(), Gender.NEUTER, "templ",
                                                       "templum", "templī", "templō", "templum", "templō", "templum",
                                                       "templa", "templōrum", "templīs", "templa", "templīs", "templa");

        assertTrue(ONounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(ONounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(ONounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.MASCULINE, ONounDeclension.getInstance().getPrimaryGender());
    }
    
}
