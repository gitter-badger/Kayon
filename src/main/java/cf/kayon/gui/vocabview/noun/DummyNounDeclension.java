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

package cf.kayon.gui.vocabview.noun;

import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents {@code null} in the list of declensions in the {@link NounView}.
 *
 * @since 0.0.1
 */
public class DummyNounDeclension implements NounDeclension
{
    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    public static final DummyNounDeclension INSTANCE = new DummyNounDeclension();

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private DummyNounDeclension() {}

    /**
     * Gets the only instance of this as specified by {@link cf.kayon.core.noun.NounDeclension}.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    public static DummyNounDeclension getInstance()
    {
        return INSTANCE;
    }

    /**
     * @since 0.0.1
     */
    @Nullable
    @Override
    public Gender getPrimaryGender()
    {
        return Gender.MASCULINE;
    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public String determineRootWord(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 0.0.1
     */
    @Override
    public boolean allowsGender(@NotNull Gender genderToCheck)
    {
        throw new UnsupportedOperationException();
    }
}
