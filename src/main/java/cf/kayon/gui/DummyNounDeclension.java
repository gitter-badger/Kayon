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

package cf.kayon.gui;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DummyNounDeclension implements NounDeclension
{
    public static final DummyNounDeclension INSTANCE = new DummyNounDeclension();

    public static DummyNounDeclension getInstance()
    {
        return INSTANCE;
    }

    private DummyNounDeclension() {}

    @Nullable
    @Override
    public Gender getPrimaryGender()
    {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String decline(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String determineRootWord(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean allowsGender(@NotNull Gender genderToCheck)
    {
        throw new UnsupportedOperationException();
    }
}
