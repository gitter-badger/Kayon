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

import cf.kayon.core.Case;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.util.StringUtil;
import com.google.common.base.Strings;
import cf.kayon.core.Count;
import cf.kayon.core.FormingException;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.assertEquals;

public class NounDeclensionTestingUtil
{

    private static final Logger LOGGER = LoggerFactory.getLogger(NounDeclensionTestingUtil.class);

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
        LOGGER.info("Class:         " + nounDeclension.getClass().getName());
        LOGGER.info("Expected root: " + expectedRootWord);
        LOGGER.info("Gender:        " + genderOfNoun);
        Iterator<Pair<String, Boolean>> iterator = new ObjectArrayIterator<>(formsToTest);
        for (Count count : Count.values())
        {
            LOGGER.info("");
            for (Case currentCase : Case.values())
            {
                Pair<String, Boolean> currentPair = iterator.next();
                if (currentPair.getRight())
                {
                    LOGGER.info(Strings.padEnd(currentCase.toString(), 12, ' ') + " FINE Expected: " + Strings.padEnd(currentPair.getLeft(), 25, ' ') + expectedRootWord);
                    String declinedForm = nounDeclension.decline(currentCase, count, genderOfNoun, expectedRootWord);
                    String determinedRootWord = nounDeclension.determineRootWord(currentCase, count, genderOfNoun, currentPair.getLeft());
                    LOGGER.info(Strings.padEnd(count.toString(), 18, ' ') + "Got:      " + Strings.padEnd(declinedForm, 25, ' ') + determinedRootWord);
                    assertSpecialEquals(currentPair.getLeft(), declinedForm);
                    assertSpecialEquals(expectedRootWord, determinedRootWord);
                } else
                {
                    LOGGER.info(Strings.padEnd(currentCase.toString(), 12, ' ') + " EXCE Expected: " + Strings.padEnd(currentPair.getLeft(), 25, ' ') + expectedRootWord);
                    Throwable throwable1 = exceptionThrownBy(() -> nounDeclension.decline(currentCase, count, genderOfNoun, expectedRootWord), FormingException.class);
                    Throwable throwable2 =
                            exceptionThrownBy(() -> nounDeclension.determineRootWord(currentCase, count, genderOfNoun, currentPair.getLeft()), FormingException.class);
                    LOGGER.info(Strings.padEnd(count.toString(), 18, ' ') + "Got:      " + Strings.padEnd(throwable1.getClass().getSimpleName(), 25, ' ') +
                                throwable2.getClass().getSimpleName());
                }
            }
        }
        LOGGER.info("");
    }
}
