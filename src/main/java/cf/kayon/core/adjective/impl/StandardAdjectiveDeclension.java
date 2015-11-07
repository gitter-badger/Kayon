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

/**
 * The base abstract implementation for all AdjectiveDeclensions implementations of this package.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public abstract class StandardAdjectiveDeclension implements AdjectiveDeclension
{

    /**
     * Selects the correct positive ending for a specified form.
     * <p>
     * Only returns lowercase endings (see annotation).
     *
     * @param caze   The case.
     * @param count  The count.
     * @param gender The gender.
     * @return An ending. {@code null} if there is no standard ending for this form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @Nullable
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    protected abstract String selectCorrectPositiveEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender);

    /**
     * Selects the correct ending for a specified form.
     * <p>
     * Only returns lowercase endings (see annotation).
     *
     * @param adjectiveForm The adjective form.
     * @return An ending. Never null.
     * @throws FormingException     If the ending could not be determined.
     * @throws NullPointerException If any of the arguments is null.
     * @since 0.0.1
     */
    @NotNull
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public String selectCorrectEnding(@NotNull AdjectiveForm adjectiveForm)
            throws FormingException
    {
        @Nullable
        String endingOrNull = null;
        switch (adjectiveForm.getComparisonDegree())
        {
            case POSITIVE: // Choose from this AdjectiveDeclension
                endingOrNull = selectCorrectPositiveEndingOrNull(adjectiveForm.getCase(), adjectiveForm.getCount(), adjectiveForm.getGender());
                break;
            case COMPARATIVE: // Choose from a preset of endings (always)
                endingOrNull = AdjectiveDeclensionUtil.endingsComparative.get(adjectiveForm.getGender(), adjectiveForm.getCount()).get(adjectiveForm.getCase());
                break;
            case SUPERLATIVE: // Choose from a preset of endings (always)
                endingOrNull = AdjectiveDeclensionUtil.endingsSuperlative.get(adjectiveForm.getGender(), adjectiveForm.getCount()).get(adjectiveForm.getCase());
                break;
            default:
                break;
        }
        if (endingOrNull == null)
            throw new FormingException();
        return endingOrNull;
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    @Override
    public String decline(@NotNull AdjectiveForm adjectiveForm, @NotNull String rootWord)
            throws FormingException
    {
        if (adjectiveForm.getComparisonDegree() == ComparisonDegree.SUPERLATIVE)
        {
            String unSpecialRootWord = StringUtil.unSpecialString(rootWord);
            return rootWord + (unSpecialRootWord.endsWith("er") ? "rim" : unSpecialRootWord.endsWith("l") ? "lim" : "issim") +
                   selectCorrectEnding(adjectiveForm);
        }
        return rootWord + selectCorrectEnding(adjectiveForm);
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    @Override
    public String determineRootWord(
            @NotNull AdjectiveForm adjectiveForm, @NotNull String declinedForm)
            throws FormingException
    {
        if (adjectiveForm.getComparisonDegree() == ComparisonDegree.SUPERLATIVE)
        {
            @NotNull
            String chosenEnding = selectCorrectEnding(adjectiveForm);

            @Nullable
            String attempt = FormingUtil.determineRootWordOrNull(declinedForm, "issim" + chosenEnding);
            if (attempt != null)
                return attempt;

            return FormingUtil.determineRootWord(declinedForm, "im" + chosenEnding);
        }
        return FormingUtil.determineRootWord(declinedForm, selectCorrectEnding(adjectiveForm));
    }

    /**
     * @since 0.0.1
     */
    @Nullable
    @Override
    public Set<AdjectiveForm> getEqualForms(@NotNull AdjectiveForm adjectiveForm)
    {
        return getEqualForms(adjectiveForm, true);
    }

    /**
     * Gets all equal forms to a specified form.
     *
     * @param adjectiveForm The adjective form.
     * @param goRecursive      Whether to include equal forms of equal forms.
     * @return A Set of adjective forms.
     * @since 0.0.1
     */
    @Nullable
    private Set<AdjectiveForm> getEqualForms(
            @NotNull AdjectiveForm adjectiveForm, boolean goRecursive)
    {
        checkNotNull(adjectiveForm);

        // First, prepare a set that is a) empty or b) filled with forms from implementation
        @NotNull
        Set<AdjectiveForm> workingSet;
        if (adjectiveForm.getComparisonDegree() == ComparisonDegree.POSITIVE)
        {
            Set<AdjectiveForm> formsFromImplementation = this.getEqualFormsPositive(adjectiveForm.getCase(), adjectiveForm.getCount(), adjectiveForm.getGender());
            if (formsFromImplementation != null)
                workingSet = formsFromImplementation;
            else
                workingSet = Sets.newHashSet();
        } else
        {
            workingSet = Sets.newHashSet();
        }

        // Add the form itself
        workingSet.add(adjectiveForm);

        // Now, add neuter rules
        if (adjectiveForm.getGender() == Gender.NEUTER &&
            (adjectiveForm.getCase() == Case.NOMINATIVE || adjectiveForm.getCase() == Case.ACCUSATIVE || adjectiveForm.getCase() == Case.VOCATIVE))
        {
            workingSet.add(AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.NOMINATIVE));
            workingSet.add(AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.ACCUSATIVE));
            workingSet.add(AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.VOCATIVE));
        }

        // Now, add vocative rules
        if ((adjectiveForm.getComparisonDegree() != ComparisonDegree.POSITIVE) ||
            (applyPositiveVocativeEquals(adjectiveForm.getCount()) && ((adjectiveForm.getCase() == Case.NOMINATIVE) || (adjectiveForm.getCase() == Case.VOCATIVE))))
        {
            workingSet.add(AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.NOMINATIVE));
            workingSet.add(AdjectiveForm.of(adjectiveForm.getComparisonDegree(), adjectiveForm.getCount(), adjectiveForm.getGender(), Case.VOCATIVE));
        }

        // Now, recursively (depth = 1) add equal forms of equal forms
        if (goRecursive)
            workingSet.forEach(form -> {
                Set<AdjectiveForm> currentEquals =
                        StandardAdjectiveDeclension.this.getEqualForms(adjectiveForm, false);
                if (currentEquals != null)
                    workingSet.addAll(currentEquals);
            });

        return workingSet.size() <= 1 ? null : workingSet; // Because it is not backed anywhere, it's okay to return the mutable HashSet
    }

    /**
     * Gets all equal forms to a specified positive form.
     * <p>
     * This method is not supposed to handle neuter rules, those are applied afterwards by {@link #getEqualForms(AdjectiveForm)}.
     * <br>
     * It is perfectly valid to only have a method {@code return null;} if that actually applies for the implementation. In that case, it is recommended to,
     * for semantic reasons, include {@link com.google.common.base.Preconditions#checkNotNull(Object) checkNotNull(Object)} calls for all the arguments as well.
     *
     * @param caze   The {@link Case} of the form to get the equal forms to.
     * @param count  The {@link Count} of the form to get the equal forms to.
     * @param gender The {@link Gender} of the form to ger the equal forms to.
     * @return A {@link Set} of {@link AdjectiveForm AdjectiveForms} that are exactly equal in forming. If there are no equal forms, return {@code null}.
     * <strong>The returned {@link Set} may not be immutable.</strong>
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @Nullable
    protected abstract Set<AdjectiveForm> getEqualFormsPositive(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender);

    /**
     * Returns whether this AdjectiveDeclension's positive vocative and nominative forms are equal.
     *
     * @param count The count to get to whether the positive vocative and nominative forms are equal.
     * @return {@code true} or {@code false} as described above.
     * @throws NullPointerException If {@code count} is {@code null}.
     * @since 0.0.1
     */
    protected abstract boolean applyPositiveVocativeEquals(@NotNull Count count);
}
