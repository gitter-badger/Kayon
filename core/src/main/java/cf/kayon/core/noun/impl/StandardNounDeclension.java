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

import cf.kayon.core.CaseHandling;
import cf.kayon.core.FormingException;
import cf.kayon.core.FormingUtil;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base abstract implementation for all NounDeclension implementations of this package.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public abstract class StandardNounDeclension implements NounDeclension
{

    /**
     * Selects the correct ending for a specified form.
     * <p>
     * Only returns lowercase endings (see annotation).
     * <p>
     * Does not throw an unchecked exception (as documented in {@link NounDeclension#allowsGender(Gender)}) if the {@code gender} is disallowed,
     * instead returns a arbitrary possibly wrong ending.
     *
     * @param nounForm The noun form.
     * @param gender   The gender.
     * @return An ending. {@code null} if there is no standard ending for this form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    protected abstract String selectCorrectEndingOrNull(@NotNull NounForm nounForm, @NotNull Gender gender);

    /**
     * Selects the correct ending for a specified form.
     * <p>
     * Only returns lowercase endings (see annotation).
     * <p>
     * Does not throw an unchecked exception (as documented in {@link NounDeclension#allowsGender(Gender)}) if the {@code gender} is disallowed,
     * instead returns a arbitrary possibly wrong ending.
     *
     * @param nounForm The noun form.
     * @param gender   The gender.
     * @return An ending.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @throws FormingException     If there is no standard ending for this form.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    public String selectCorrectEnding(@NotNull NounForm nounForm, @NotNull Gender gender) throws FormingException
    {
        @Nullable
        String selectedCorrectEndingsOrNull = this.selectCorrectEndingOrNull(nounForm, gender);
        if (selectedCorrectEndingsOrNull == null)
            throw new FormingException("Forming failure for form " + nounForm);
        return selectedCorrectEndingsOrNull;
    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        return rootWord + selectCorrectEnding(nounForm, gender);
    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public String determineRootWord(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        return FormingUtil.determineRootWord(declinedForm, this.selectCorrectEnding(nounForm, gender), nounForm);
    }
}
