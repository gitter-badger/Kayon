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

package cf.kayon.core.adjective;

import cf.kayon.core.Case;
import cf.kayon.core.Gender;
import com.google.common.collect.Lists;
import cf.kayon.core.Count;
import cf.kayon.core.adjective.impl.ITwoEndAdjectiveDeclension;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdjectiveTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdjectiveTest.class);

    @Test
    public void testDeclining()
    {
        Adjective adjective = new Adjective(ITwoEndAdjectiveDeclension.getInstance(), "grav");
        List<String> expected =
                Lists.newArrayList("gravis", "gravis", "gravi", "gravem", "gravi", "gravis", "graves", "gravium", "gravibus", "graves", "gravibus", "graves",
                                   "gravis", "gravis", "gravi", "gravem", "gravi", "gravis", "graves", "gravium", "gravibus", "graves", "gravibus", "graves",
                                   "grave", "gravis", "gravi", "grave", "gravi", "grave", "gravia", "gravium", "gravibus", "gravia", "gravibus", "gravia");


    }

    private static void testAdjectiveForms(@NotNull List<String> expected)
    {
        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
        {
            LOGGER.info(comparisonDegree.toString());
            for (Gender gender : Gender.values())
            {
                LOGGER.info(gender.toString());
                for (Count count : Count.values())
                {
                    LOGGER.info(count.toString());
                    for (Case caze : Case.values())
                    {
                        // TODO implementation
                    }
                }
            }
        }
    }
}