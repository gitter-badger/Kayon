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
import cf.kayon.core.CaseHandling;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.adjective.AdjectiveDeclensionUtil;
import cf.kayon.core.adjective.AdjectiveForm;
import cf.kayon.core.adjective.ComparisonDegree;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The implementation for the third adjective declension, i-stem-adjectives, two-ending.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class ITwoEndAdjectiveDeclension extends StandardAdjectiveDeclension
{

    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final ITwoEndAdjectiveDeclension INSTANCE = new ITwoEndAdjectiveDeclension();

    /**
     * The endings for the positive forms.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Table<Gender, Count, Map<Case, String>> positiveEndingsTable =
            AdjectiveDeclensionUtil.endingsTable("is", "is", "i", "em", "ī", "is", "es", "ium", "ibus", "ēs", "ibus", "es",
                                                 "is", "is", "i", "em", "ī", "is", "es", "ium", "ibus", "ēs", "ibus", "es",
                                                 "e", "is", "i", "e", "ī", "e", "ia", "ium", "ibus", "ia", "ibus", "ia");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private ITwoEndAdjectiveDeclension() {}

    /**
     * Gets the only instance of this ITwoEndAdjectiveDeclension.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    @NotNull
    public static ITwoEndAdjectiveDeclension getInstance()
    {
        return INSTANCE;
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    @Override
    protected String selectCorrectPositiveEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return positiveEndingsTable.get(gender, count).get(caze);
    }

    /**
     * @since 0.0.1
     */
    @Nullable
    @Override
    protected Set<AdjectiveForm> getEqualFormsPositive(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        if (caze == Case.NOMINATIVE || caze == Case.ACCUSATIVE)
        {
            if (gender == Gender.NEUTER)
                return null; // Do not handle neuter rules, StandardAdjectiveDeclension applies those
            // Whole "row" except the neuter form is equal
            return Sets.newHashSet(AdjectiveForm.of(ComparisonDegree.POSITIVE, count, Gender.MASCULINE, caze),
                                   AdjectiveForm.of(ComparisonDegree.POSITIVE, count, Gender.FEMININE, caze));
        }
        // Whole "row" is equal
        return Sets.newHashSet(AdjectiveForm.of(ComparisonDegree.POSITIVE, count, Gender.MASCULINE, caze),
                               AdjectiveForm.of(ComparisonDegree.POSITIVE, count, Gender.FEMININE, caze),
                               AdjectiveForm.of(ComparisonDegree.POSITIVE, count, Gender.NEUTER, caze));
    }

    /**
     * @since 0.0.1
     */
    @Override
    protected boolean applyPositiveVocativeEquals(@NotNull Count count)
    {
        checkNotNull(count);
        return true;
    }
}
