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
 * The implementation for the fourth noun declension.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class ENounDeclension extends StandardNounDeclension
{
    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    private static final ENounDeclension INSTANCE = new ENounDeclension();

    /**
     * The endings.
     *
     * @since 0.0.1
     */
    private final Map<NounForm, String> endings = NounDeclensionUtil.endingsMap("ēs", "eī", "eī", "em", "ē", "ēs",
                                                                                "ēs", "ērum", "ēbus", "ēs", "ēbus", "ēs");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private ENounDeclension() {}

    /**
     * Gets the only instance of this as specified by {@link cf.kayon.core.noun.NounDeclension}.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    public static ENounDeclension getInstance()
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
        //        if (nounForm.getCount() == Count.SINGULAR && (nounForm.getCase() == Case.GENITIVE || nounForm.getCase() == Case.DATIVE))
        //            return "ēī";
        return endings.get(nounForm);
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    @Override
    public String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        checkNotNull(nounForm);
        checkNotNull(gender);
        checkNotNull(rootWord);
        if (nounForm.getCount() == Count.SINGULAR && (nounForm.getCase() == Case.GENITIVE || nounForm.getCase() == Case.DATIVE) &&
            StringUtil.unSpecialString(rootWord).endsWith("i")) // Most expensive check last
            return rootWord + "ēī";
        return rootWord + selectCorrectEnding(nounForm, gender);
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
}
