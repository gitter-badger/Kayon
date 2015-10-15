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
import cf.kayon.core.Gender;
import cf.kayon.core.adjective.AdjectiveDeclensionUtil;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import cf.kayon.core.Count;
import cf.kayon.core.adjective.AdjectiveForm;
import cf.kayon.core.adjective.ComparisonDegree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class IOneEndAdjectiveDeclension extends StandardAdjectiveDeclension
{

    @NotNull
    private static final IOneEndAdjectiveDeclension INSTANCE = new IOneEndAdjectiveDeclension();

    @NotNull
    private final Table<Gender, Count, Map<Case, String>> positiveEndingsTable =
            AdjectiveDeclensionUtil.endingsTable("s", "is", "i", "em", "ī", "s", "es", "ium", "ibus", "ēs", "ibus", "es",
                                                 "s", "is", "i", "em", "ī", "s", "es", "ium", "ibus", "ēs", "ibus", "es",
                                                 "s", "is", "i", "s", "ī", "s", "ia", "ium", "ibus", "ia", "ibus", "ia");

    private IOneEndAdjectiveDeclension() {}

    @NotNull
    public static IOneEndAdjectiveDeclension getInstance()
    {
        return INSTANCE;
    }

    @Nullable
    @Override
    protected String selectCorrectPositiveEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return positiveEndingsTable.get(gender, count).get(caze);
    }

    @Nullable
    @Override
    protected Set<AdjectiveForm> getEqualFormsPositive(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        if ((count == Count.SINGULAR && caze == Case.ACCUSATIVE) || (count == Count.PLURAL && (caze == Case.NOMINATIVE || caze == Case.ACCUSATIVE)))
        {
            if (gender == Gender.NEUTER)
                return null; // Do not handle neuter rules, StandardAdjectiveDeclension applies those
            // Whole "row" except the neuter form is equal
            return Sets.newHashSet(new AdjectiveForm(ComparisonDegree.POSITIVE, caze, count, Gender.MASCULINE),
                                   new AdjectiveForm(ComparisonDegree.POSITIVE, caze, count, Gender.FEMININE));
        }
        // Whole "row" is equal
        return Sets.newHashSet(new AdjectiveForm(ComparisonDegree.POSITIVE, caze, count, Gender.MASCULINE),
                               new AdjectiveForm(ComparisonDegree.POSITIVE, caze, count, Gender.FEMININE),
                               new AdjectiveForm(ComparisonDegree.POSITIVE, caze, count, Gender.NEUTER));
    }

    @Override
    protected boolean applyVocativeEquals(@NotNull Count count)
    {
        checkNotNull(count);
        return true;
    }
}
