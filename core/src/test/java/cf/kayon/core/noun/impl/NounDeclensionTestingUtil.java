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
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounForm;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;

public class NounDeclensionTestingUtil
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(NounDeclensionTestingUtil.class);

    public static void testCorrectDeclining(
            @NotNull NounDeclension nounDeclension, @NotNull Gender genderOfNoun, @NotNull String expectedRootWord, Object... testTargets)
            throws FormingException
    {
        int formCounter = 0;
        for (int targetCounter = 0; targetCounter < testTargets.length; targetCounter++)
        {
            String currentExpectedForm = (String) testTargets[targetCounter];
            NounForm currentNounForm = NounForm.values().get(formCounter++);
            Object next = targetCounter + 1 < testTargets.length ? testTargets[targetCounter + 1] : null;

            try
            {
                if (next instanceof Boolean && !((boolean) next))
                {
                    targetCounter++;
                    FormingException fe1 = exceptionThrownBy(() -> nounDeclension.decline(currentNounForm, genderOfNoun, expectedRootWord), FormingException.class);
                    assertNotNull(fe1);
                    assertEquals("Forming failure for form " + currentNounForm, fe1.getMessage());
                    assertEquals("Forming failure for form " + currentNounForm, fe1.getLocalizedMessage());
                    assertNull(fe1.getCause());

                    FormingException fe2 =
                            exceptionThrownBy(() -> nounDeclension.determineRootWord(currentNounForm, genderOfNoun, currentExpectedForm), FormingException.class);
                    assertNotNull(fe2);
                    assertEquals("Forming failure for form " + currentNounForm, fe2.getMessage());
                    assertEquals("Forming failure for form " + currentNounForm, fe2.getLocalizedMessage());
                    assertNull(fe2.getCause());
                } else
                {
                    String declinedForm = nounDeclension.decline(currentNounForm, genderOfNoun, expectedRootWord);
                    String determinedRootWord = nounDeclension.determineRootWord(currentNounForm, genderOfNoun, currentExpectedForm);
                    assertEquals(currentExpectedForm, declinedForm);
                    assertEquals(expectedRootWord, determinedRootWord);
                }
            } catch (Throwable t)
            {
                LOGGER.error(String.format("%s when at:%n    targetCounter=%d%n    formCounter=%d%n    currentExpectedForm=%s%n    currentNounForm=%s%n    next=%s",
                                           t.getClass().getSimpleName(), targetCounter, formCounter, currentExpectedForm, currentNounForm, next));
                throw t;
            }
        }
    }
}
