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

public abstract class StandardNounDeclension implements NounDeclension
{

    @Nullable
    protected abstract String selectCorrectEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender);

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

    @NotNull
    @Override
    public String decline(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        return rootWord + selectCorrectEnding(caze, count, gender);
    }

    @NotNull
    @Override
    public String determineRootWord(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        return FormingUtil.determineRootWord(declinedForm, this.selectCorrectEnding(caze, count, gender));
    }
}
