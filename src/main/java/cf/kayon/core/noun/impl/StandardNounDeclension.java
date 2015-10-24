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

import cf.kayon.core.*;
import cf.kayon.core.noun.NounDeclension;
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
     * @param caze   The case.
     * @param count  The count.
     * @param gender The gender.
     * @return An ending. {@code null} if there is no standard ending for this form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    protected abstract String selectCorrectEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender);

    /**
     * Selects the correct ending for a specified form.
     * <p>
     * Only returns lowercase endings (see annotation).
     * <p>
     * Does not throw an unchecked exception (as documented in {@link NounDeclension#allowsGender(Gender)}) if the {@code gender} is disallowed,
     * instead returns a arbitrary possibly wrong ending.
     *
     * @param caze   The case.
     * @param count  The count.
     * @param gender The gender.
     * @return An ending.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @throws FormingException     If there is no standard ending for this form.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    public String selectCorrectEnding(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender) throws FormingException
    {
        @Nullable
        String selectedCorrectEndingsOrNull = this.selectCorrectEndingOrNull(caze, count, gender);
        if (selectedCorrectEndingsOrNull == null)
            throw new FormingException();
        return selectedCorrectEndingsOrNull;
    }

    //    @NotNull
    //    @Override
    //    public List<String> getPossibleForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    //    {
    //        List<String> buffer = getAlternateForms(caze, count, gender, rootWord);
    //        if (buffer == null)
    //            buffer = new ArrayList<>(1);
    //        try
    //        {
    //            buffer.add(this.decline(caze, count, gender, rootWord));
    //        } catch (FormingException ignored) {}
    //        if (buffer.isEmpty())
    //            throw new FormingException("Neither any main nor alternative forms!");
    //        return buffer;
    //    }

    //    @Nullable
    //    @Override
    //    public List<String> getAlternateForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
    //    {
    //        return null;
    //    }
    //
    //    @Override
    //    public boolean hasAlternateForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
    //    {
    //        return getAlternateForms(caze, count, gender, rootWord) != null;
    //    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public String decline(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        return rootWord + selectCorrectEnding(caze, count, gender);
    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public String determineRootWord(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        return FormingUtil.determineRootWord(declinedForm, this.selectCorrectEnding(caze, count, gender));
    }
}
