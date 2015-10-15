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

import cf.kayon.core.*;
import cf.kayon.core.adjective.AdjectiveDeclension;
import cf.kayon.core.adjective.AdjectiveDeclensionUtil;
import cf.kayon.core.adjective.AdjectiveForm;
import cf.kayon.core.adjective.ComparisonDegree;
import cf.kayon.core.util.StringUtil;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class StandardAdjectiveDeclension implements AdjectiveDeclension
{

    @Nullable
    protected abstract String selectCorrectPositiveEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender);

    @NotNull
    public String selectCorrectEnding(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
            throws FormingException
    {
        @Nullable
        String endingOrNull = null;
        switch (comparisonDegree)
        {
            case POSITIVE: // Choose from this AdjectiveDeclension
                endingOrNull = selectCorrectPositiveEndingOrNull(caze, count, gender);
                break;
            case COMPARATIVE: // Choose from a preset of endings (always)
                endingOrNull = AdjectiveDeclensionUtil.endingsComparative.get(gender, count).get(caze);
                break;
            case SUPERLATIVE: // Choose from a preset of endings (always)
                endingOrNull = AdjectiveDeclensionUtil.endingsSuperlative.get(gender, count).get(caze);
                break;
            default: break;
        }
        if (endingOrNull == null)
            throw new FormingException();
        return endingOrNull;
    }

    @NotNull
    @Override
    public String decline(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
            throws FormingException
    {
        if (comparisonDegree == ComparisonDegree.SUPERLATIVE)
        {
            String unSpecialRootWord = StringUtil.unSpecialString(rootWord);
            return rootWord + (unSpecialRootWord.endsWith("er") ? "rim" : unSpecialRootWord.endsWith("l") ? "lim" : "issim") +
                   selectCorrectEnding(comparisonDegree, caze, count, gender);
        }
        return rootWord + selectCorrectEnding(comparisonDegree, caze, count, gender);
    }

    @NotNull
    @Override
    public String determineRootWord(
            @NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm)
            throws FormingException
    {
        if (comparisonDegree == ComparisonDegree.SUPERLATIVE)
        {
            @NotNull
            String chosenEnding = selectCorrectEnding(comparisonDegree, caze, count, gender);

            @Nullable
            String attempt = FormingUtil.determineRootWordOrNull(declinedForm, "issim" + chosenEnding);
            if (attempt != null)
                return attempt;

            return FormingUtil.determineRootWord(declinedForm, "im" + chosenEnding);
        }
        return FormingUtil.determineRootWord(declinedForm, selectCorrectEnding(comparisonDegree, caze, count, gender));
    }

    @Nullable
    @Override
    public Set<AdjectiveForm> getEqualForms(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return getEqualForms(comparisonDegree, caze, count, gender, true);
    }

    @Nullable
    private Set<AdjectiveForm> getEqualForms(
            @NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, boolean goRecursive)
    {
        checkNotNull(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        // First, prepare a set that is a) empty or b) filled with forms from implementation
        @NotNull
        Set<AdjectiveForm> workingSet;
        if (comparisonDegree == ComparisonDegree.POSITIVE)
        {
            Set<AdjectiveForm> formsFromImplementation = this.getEqualFormsPositive(caze, count, gender);
            if (formsFromImplementation != null)
                workingSet = formsFromImplementation;
            else
                workingSet = Sets.newHashSet();
        } else
        {
            workingSet = Sets.newHashSet();
        }

        // Add the form itself
        workingSet.add(new AdjectiveForm(comparisonDegree, caze, count, gender));

        // Now, add neuter rules
        if (gender == Gender.NEUTER && (caze == Case.NOMINATIVE || caze == Case.ACCUSATIVE || caze == Case.VOCATIVE))
        {
            workingSet.add(new AdjectiveForm(comparisonDegree, Case.NOMINATIVE, count, gender));
            workingSet.add(new AdjectiveForm(comparisonDegree, Case.ACCUSATIVE, count, gender));
            workingSet.add(new AdjectiveForm(comparisonDegree, Case.VOCATIVE, count, gender));
        }

        // Now, add vocative rules
        if (applyVocativeEquals(count) && (caze == Case.NOMINATIVE || caze == Case.VOCATIVE))
        {
            workingSet.add(new AdjectiveForm(comparisonDegree, Case.NOMINATIVE, count, gender));
            workingSet.add(new AdjectiveForm(comparisonDegree, Case.VOCATIVE, count, gender));
        }

        // Now, recursively (depth 1) add equal forms of equal forms
        if (goRecursive)
            workingSet.forEach(form -> {
                Set<AdjectiveForm> currentEquals =
                        StandardAdjectiveDeclension.this.getEqualForms(form.getComparisonDegree(), form.getCase(), form.getCount(), form.getGender(), false);
                if (currentEquals != null)
                    workingSet.addAll(currentEquals);
            });

        return workingSet.size() <= 1 ? null : workingSet; // Because it is now backed anywhere, it's okay to return the mutable HashSet
    }

    /**
     * Do not handle neuter rules, those are applied afterwards by {@link #getEqualForms(ComparisonDegree, Case, Count, Gender)}.
     * <br>
     * It is perfecly valid to only have a method {@code return null;} if that actually applies for the implementation. In that case, it is recommended to,
     * for semantic reasons, include {@link com.google.common.base.Preconditions#checkNotNull(Object) checkNotNull(Object)} calls for all the arguments.
     *
     * @param caze   The {@link Case} of the form to get the equal forms to.
     * @param count  The {@link Count} of the form to get the equal forms to.
     * @param gender The {@link Gender} of the form to ger the equal forms to.
     * @return A {@link Set} of {@link AdjectiveForm AdjectiveForms} that are exactly equal in forming. If there are no equal forms, return {@code null}.
     * The returned {@link Set} may not be immutable.
     * @since 0.0.1
     */
    @Nullable
    protected abstract Set<AdjectiveForm> getEqualFormsPositive(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender);

    protected abstract boolean applyVocativeEquals(@NotNull Count count);
}
