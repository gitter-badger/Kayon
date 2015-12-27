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

public class UNounDeclensionTest
{

    @Test
    public void testUNounDeclension() throws Exception
    {
        NounDeclensionTestingUtil.testCorrectDeclining(UNounDeclension.getInstance(), Gender.MASCULINE, "port",
                                                       "portus", "portūs", "portuī", "portum", "portū", "portus",
                                                       "portūs", "portuum", "portibus", "portūs", "portibus", "portūs");

        NounDeclensionTestingUtil.testCorrectDeclining(UNounDeclension.getInstance(), Gender.FEMININE, "man",
                                                       "manus", "manūs", "manuī", "manum", "manū", "manus",
                                                       "manūs", "manuum", "manibus", "manūs", "manibus", "manūs");

        NounDeclensionTestingUtil.testCorrectDeclining(UNounDeclension.getInstance(), Gender.NEUTER, "corn",
                                                       "cornū", "cornūs", "cornū", "cornū", "cornū", "cornū",
                                                       "cornua", "cornuum", "cornibus", "cornua", "cornibus", "cornua");

        assertTrue(UNounDeclension.getInstance().allowsGender(Gender.MASCULINE));
        assertTrue(UNounDeclension.getInstance().allowsGender(Gender.FEMININE));
        assertTrue(UNounDeclension.getInstance().allowsGender(Gender.NEUTER));

        assertSame(Gender.MASCULINE, UNounDeclension.getInstance().getPrimaryGender());
    }
}
