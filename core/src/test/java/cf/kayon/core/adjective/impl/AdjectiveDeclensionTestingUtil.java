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

package cf.kayon.core.adjective.impl;

import cf.kayon.core.Case;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import cf.kayon.core.adjective.AdjectiveDeclension;
import cf.kayon.core.adjective.AdjectiveForm;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class AdjectiveDeclensionTestingUtil
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(AdjectiveDeclensionTestingUtil.class);

    public static void testCorrectDeclining(@NotNull AdjectiveDeclension adjectiveDeclension, @NotNull String expectedRootWord, @NotNull Object... testTargets)
            throws FormingException
    {
        int formCounter = 0;
        for (int targetCounter = 0; targetCounter < testTargets.length; targetCounter++)
        {
            String currentExpectedForm = (String) testTargets[targetCounter];
            AdjectiveForm currentAdjectiveForm = AdjectiveForm.values().get(formCounter++);
            Object next = targetCounter + 1 < testTargets.length ? testTargets[targetCounter + 1] : null;

            try
            {
                if (next instanceof Boolean && !((boolean) next))
                {
                    targetCounter++;
                    FormingException ex1 = exceptionThrownBy(() -> adjectiveDeclension.decline(currentAdjectiveForm, expectedRootWord), FormingException.class);
                    assertNotNull(ex1);
                    assertNull(ex1.getCause());
                    assertEquals("Forming failure for form " + currentAdjectiveForm, ex1.getMessage());
                    assertEquals("Forming failure for form " + currentAdjectiveForm, ex1.getLocalizedMessage());

                    FormingException ex2 =
                            exceptionThrownBy(() -> adjectiveDeclension.determineRootWord(currentAdjectiveForm, currentExpectedForm), FormingException.class);
                    assertNotNull(ex2);
                    assertNull(ex1.getCause());
                    assertEquals("Forming failure for form " + currentAdjectiveForm, ex2.getMessage());
                    assertEquals("Forming failure for form " + currentAdjectiveForm, ex2.getLocalizedMessage());
                } else
                {
                    String declinedForm = adjectiveDeclension.decline(currentAdjectiveForm, expectedRootWord);
                    String determinedRootWord = adjectiveDeclension.determineRootWord(currentAdjectiveForm, currentExpectedForm);
                    assertEquals(currentExpectedForm, declinedForm);
                    assertEquals(expectedRootWord, determinedRootWord);
                }
            } catch (Throwable t)
            {
                LOGGER.error(String.format("%s when at:%n    targetCounter=%d%n    formCounter=%d%n    currentExpectedForm=%s%n    currentAdjectiveForm=%s%n    next=%s",
                                           t.getClass().getSimpleName(), targetCounter, formCounter, currentExpectedForm, currentAdjectiveForm, next));
                throw t;
            }
        }
    }

    public static void assertStandardEquals(AdjectiveForm adjectiveForm, Set<AdjectiveForm> set)
    {
        if (adjectiveForm.getGender() == Gender.NEUTER &&
            (adjectiveForm.getCase() == Case.NOMINATIVE || adjectiveForm.getCase() == Case.ACCUSATIVE || adjectiveForm.getCase() == Case.VOCATIVE))
        {
            assertThat(set, hasItems(
                    AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), Gender.NEUTER, Case.NOMINATIVE),
                    AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), Gender.NEUTER, Case.ACCUSATIVE),
                    AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), Gender.NEUTER, Case.VOCATIVE)
            ));
        }
    }

    public static void assertVocativeEquals(AdjectiveForm adjectiveForm, Set<AdjectiveForm> set)
    {
        assertStandardEquals(adjectiveForm, set);
        if (adjectiveForm.getCase() == Case.NOMINATIVE || adjectiveForm.getCase() == Case.VOCATIVE)
            assertThat(set, hasItems(
                    AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.NOMINATIVE),
                    AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.VOCATIVE)
            ));
    }
}
