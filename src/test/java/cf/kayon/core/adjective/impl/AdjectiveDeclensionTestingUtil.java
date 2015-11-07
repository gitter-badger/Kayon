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
import cf.kayon.core.noun.impl.NounDeclensionTestingUtil;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class AdjectiveDeclensionTestingUtil
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AdjectiveDeclensionTestingUtil.class);

    @SafeVarargs
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void testCorrectDeclining(
            @NotNull AdjectiveDeclension adjectiveDeclension, @NotNull String expectedRootWord, @NotNull Pair<String, Boolean>... formsToTest) throws FormingException
    {
        Iterator<Pair<String, Boolean>> iterator = new ObjectArrayIterator<>(formsToTest);

        for (AdjectiveForm adjectiveForm : AdjectiveForm.values())
        {
            Pair<String, Boolean> currentPair = iterator.next();
            if (currentPair.getRight())
            {
                String declinedForm = adjectiveDeclension.decline(adjectiveForm, expectedRootWord);
                String determinedRootWord = adjectiveDeclension.determineRootWord(adjectiveForm, currentPair.getLeft());
                NounDeclensionTestingUtil.assertSpecialEquals(currentPair.getLeft(), declinedForm);
                NounDeclensionTestingUtil.assertSpecialEquals(expectedRootWord, determinedRootWord);
            } else
            {
                FormingException fe1 = exceptionThrownBy(() -> adjectiveDeclension.decline(adjectiveForm, expectedRootWord), FormingException.class);

                assertNull(fe1.getMessage());
                assertNull(fe1.getLocalizedMessage());
                assertNull(fe1.getCause());

                FormingException fe2 = exceptionThrownBy(() -> adjectiveDeclension.determineRootWord(adjectiveForm, currentPair.getLeft()), FormingException.class);

                assertNull(fe2.getMessage());
                assertNull(fe2.getLocalizedMessage());
                assertNull(fe2.getCause());
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
