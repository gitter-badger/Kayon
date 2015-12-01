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
import cf.kayon.core.noun.NounDeclensionUtil;
import cf.kayon.core.noun.NounForm;
import cf.kayon.core.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The implementation for the second noun declension, nominative singular in -us/-um.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class ONounDeclension extends StandardNounDeclension
{
    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final ONounDeclension INSTANCE = new ONounDeclension();

    /**
     * The endings for the masculine and feminine forms.
     *
     * @since 0.0.1
     */
    @NotNull
    public final Map<NounForm, String> endingsMasculineFeminine =
            NounDeclensionUtil.endingsMap("us", "ī", "ō", "um", "ō", "e",
                                          "ī", "ōrum", "īs", "ōs", "īs", "ī");

    /**
     * The endings for the neuter forms.
     *
     * @since 0.0.1
     */
    @NotNull
    public final Map<NounForm, String> endingsNeuter =
            NounDeclensionUtil.endingsMap("um", "ī", "ō", "um", "ō", "um",
                                          "a", "ōrum", "īs", "a", "īs", "a");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private ONounDeclension() {}

    /**
     * Gets the only instance of this as specified by {@link cf.kayon.core.noun.NounDeclension}.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    @NotNull
    public static ONounDeclension getInstance()
    {
        return INSTANCE;
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
        return gender == Gender.NEUTER ? endingsNeuter.get(nounForm) : endingsMasculineFeminine.get(nounForm);
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    @Override
    public String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord)
            throws FormingException
    {
        checkNotNull(nounForm);
        checkNotNull(gender);
        checkNotNull(rootWord);
        /*
         * Sample: Vocative Singular Masculine, root word is <fili>
         * Return: <filī>
         */
        if (nounForm.getCase() == Case.VOCATIVE && nounForm.getCount() == Count.SINGULAR &&
            StringUtil.unSpecialString(rootWord).endsWith("i")) // Most expensive check last (short-circuiting statement)
        {
            StringBuilder builder = new StringBuilder(rootWord);
            builder.setCharAt(builder.length() - 1, 'ī');
            return builder.toString();
        }

        return super.decline(nounForm, gender, rootWord);
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    @Override
    public String determineRootWord(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        checkNotNull(nounForm);
        checkNotNull(gender);
        checkNotNull(declinedForm);
        /*
         * Sample: Vocative Singular Masculine, form is <fili>
         * Root Word: fili (-us, -i, etc...)
         */
        if (nounForm.getCase() == Case.VOCATIVE && nounForm.getCount() == Count.SINGULAR &&
            StringUtil.unSpecialString(declinedForm).endsWith("i")) // Most expensive check last (short-circuiting statement)
        {
            StringBuilder builder = new StringBuilder(declinedForm);
            builder.setCharAt(builder.length() - 1, 'i');
            return builder.toString();
        }
        return FormingUtil.determineRootWord(declinedForm, this.selectCorrectEnding(nounForm, gender), nounForm);
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

    /**
     * @since 0.0.1
     */
    @Nullable
    @Override
    public Gender getPrimaryGender()
    {
        return Gender.MASCULINE;
    }
}
