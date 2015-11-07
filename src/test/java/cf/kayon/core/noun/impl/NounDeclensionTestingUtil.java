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
import cf.kayon.core.util.StringUtil;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.Iterator;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.assertNull;

public class NounDeclensionTestingUtil
{
    public static void assertSpecialEquals(String expected, String actual)
    {
        Assert.assertEquals(StringUtil.unSpecialString(expected), StringUtil.unSpecialString(actual));
    }

    @SafeVarargs
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void testCorrectDeclining(
            @NotNull NounDeclension nounDeclension, @NotNull Gender genderOfNoun, @NotNull String expectedRootWord, Pair<String, Boolean>... formsToTest)
            throws FormingException
    {
        Iterator<Pair<String, Boolean>> iterator = new ObjectArrayIterator<>(formsToTest);
        for (NounForm nounForm : NounForm.values())
        {
            Pair<String, Boolean> currentPair = iterator.next();
            if (currentPair.getRight()) // If declining should be expected to be successful
            {
                String declinedForm = nounDeclension.decline(nounForm, genderOfNoun, expectedRootWord);
                String determinedRootWord = nounDeclension.determineRootWord(nounForm, genderOfNoun, currentPair.getLeft());
                assertSpecialEquals(currentPair.getLeft(), declinedForm);
                assertSpecialEquals(expectedRootWord, determinedRootWord);
            } else
            {
                FormingException fe1 = exceptionThrownBy(() -> nounDeclension.decline(nounForm, genderOfNoun, expectedRootWord), FormingException.class);
                assertNull(fe1.getMessage());
                assertNull(fe1.getLocalizedMessage());
                assertNull(fe1.getCause());

                FormingException fe2 = exceptionThrownBy(() -> nounDeclension.determineRootWord(nounForm, genderOfNoun, currentPair.getLeft()), FormingException.class);
                assertNull(fe2.getMessage());
                assertNull(fe2.getLocalizedMessage());
                assertNull(fe2.getCause());
            }
        }
    }
}
