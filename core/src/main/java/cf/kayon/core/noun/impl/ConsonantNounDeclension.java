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
 * The implementation for the third noun declension, consonant stem.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class ConsonantNounDeclension extends StandardNounDeclension
{

    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final ConsonantNounDeclension INSTANCE = new ConsonantNounDeclension();

    /**
     * The endings for the masculine and feminine forms.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Map<NounForm, String> endingsMasculineFeminine =
            NounDeclensionUtil.endingsMap(null, "is", "ī", "em", "e", null,
                                          "ēs", "um", "ibus", "ēs", "ibus", "ēs");

    /**
     * The endings for the neuter forms.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Map<NounForm, String> endingsNeuter =
            NounDeclensionUtil.endingsMap(null, "is", "ī", null, "e", null,
                                          "a", "um", "ibus", "a", "ibus", "a");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private ConsonantNounDeclension() {}

    /**
     * Gets the only instance of this as specified by {@link cf.kayon.core.noun.NounDeclension}.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    @NotNull
    public static ConsonantNounDeclension getInstance()
    {
        return INSTANCE;
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    protected String selectCorrectEndingOrNull(@NotNull NounForm nounForm, @NotNull Gender gender)
    {
        checkNotNull(nounForm);
        checkNotNull(gender);
        return gender == Gender.NEUTER ? endingsNeuter.get(nounForm) : endingsMasculineFeminine.get(nounForm);
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
        checkNotNull(genderToCheck);
        return true;
    }
}
