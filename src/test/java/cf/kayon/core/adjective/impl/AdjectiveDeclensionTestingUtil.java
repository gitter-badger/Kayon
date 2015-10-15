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
import cf.kayon.core.Count;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import cf.kayon.core.adjective.AdjectiveDeclension;
import cf.kayon.core.adjective.ComparisonDegree;
import cf.kayon.core.noun.impl.NounDeclensionTestingUtil;
import com.google.common.base.Strings;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;

public class AdjectiveDeclensionTestingUtil
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AdjectiveDeclensionTestingUtil.class);

    @SafeVarargs
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void testCorrectDeclining(
            @NotNull AdjectiveDeclension adjectiveDeclension, @NotNull String expectedRootWord, @NotNull Pair<String, Boolean>... formsToTest) throws FormingException
    {
        LOGGER.info("Class:         " + adjectiveDeclension.getClass().getName());
        LOGGER.info("Expected root: " + expectedRootWord);
        Iterator<Pair<String, Boolean>> iterator = new ObjectArrayIterator<>(formsToTest);
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
                        Pair<String, Boolean> currentPair = iterator.next();
                        if (currentPair.getRight())
                        {
                            //@formatter:off because it would mess up the slightly too long lines very badly
                            LOGGER.info("  " + Strings.padEnd(caze.toString(), 12, ' ') + " FINE Expected: " + Strings.padEnd(currentPair.getLeft(), 25, ' ') + expectedRootWord);
                            String declinedForm = adjectiveDeclension.decline(comparisonDegree, caze, count, gender, expectedRootWord);
                            String determinedRootWord = adjectiveDeclension.determineRootWord(comparisonDegree, caze, count, gender, currentPair.getLeft());
                            LOGGER.info("  Got:                        " + Strings.padEnd(declinedForm, 25, ' ') + determinedRootWord);
                            NounDeclensionTestingUtil.assertSpecialEquals(currentPair.getLeft(), declinedForm);
                            NounDeclensionTestingUtil.assertSpecialEquals(expectedRootWord, determinedRootWord);
                        } else
                        {
                            LOGGER.info("  " + Strings.padEnd(caze.toString(), 12, ' ') + " EXCE Expected: " + Strings.padEnd(currentPair.getLeft(), 25, ' ') + expectedRootWord);
                            Throwable throwable1 = exceptionThrownBy(() -> adjectiveDeclension.decline(comparisonDegree, caze, count, gender, expectedRootWord), FormingException.class);
                            Throwable throwable2 = exceptionThrownBy(() -> adjectiveDeclension.determineRootWord(comparisonDegree, caze, count, gender, currentPair.getLeft()), FormingException.class);
                            LOGGER.info(Strings.padEnd(count.toString(), 18, ' ') + "  Got:                        " + Strings.padEnd(throwable1.getClass().getSimpleName(), 25, ' ') + throwable2.getClass().getSimpleName());
                            //@formatter:on
                        }
                    }
                }
            }
        }
    }
}
