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

package cf.kayon.core.noun;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NounDeclension {

    @NotNull
    default Noun declineToNoun(@NotNull String rootWord, @NotNull Gender gender) {
        return new Noun(this, gender, rootWord);
    }

    @Nullable
    Gender getPrimaryGender();

    // Throw when absolutely NO forms could be declined. If only some of them fail, simply do not return them.
    // (Theoretical, current implementations will never throw on decline()) -- add this to Javadoc
//    @NotNull
//    List<String> getPossibleForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException;

//    @Nullable
//    List<String> getAlternateForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord);
//
//    boolean hasAlternateForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord);

    @NotNull
    String decline(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException;

    @NotNull
    String determineRootWord(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException;

    boolean allowsGender(@NotNull Gender genderToCheck);

}
