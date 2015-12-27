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
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclensionUtil;
import cf.kayon.core.noun.NounForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The implementation for the first noun declension.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class ANounDeclension extends StandardNounDeclension
{
    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final ANounDeclension INSTANCE = new ANounDeclension();

    /**
     * The endings.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Map<NounForm, String> endings = NounDeclensionUtil.endingsMap("a", "ae", "ae", "am", "ā", "a",
                                                                                "ae", "ārum", "īs", "ās", "īs", "ae");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private ANounDeclension() {}

    /**
     * Gets the only instance of this as specified by {@link cf.kayon.core.noun.NounDeclension}.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    @NotNull
    public static ANounDeclension getInstance()
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
        return Gender.FEMININE;
    }

    /**
     * @since 0.0.1
     */
    @Override
    public boolean allowsGender(@NotNull Gender genderToCheck)
    {
        return checkNotNull(genderToCheck) != Gender.NEUTER;
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    @Override
    protected String selectCorrectEndingOrNull(@NotNull NounForm nounForm, @NotNull Gender gender)
    {
        checkNotNull(nounForm);
        checkNotNull(gender);
        return endings.get(nounForm);
    }
}
