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

import cf.kayon.core.Case;
import cf.kayon.core.CaseHandling;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclensionUtil;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The implementation for the third noun declension, i-stem.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class INounDeclension extends StandardNounDeclension
{
    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final INounDeclension INSTANCE = new INounDeclension();

    /**
     * The endings for the feminine forms.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Table<Case, Count, String> endingsFeminine = NounDeclensionUtil.endingsTable(null, "is", "ī", "em", "e", null,
                                                                                               "ēs", "ium", "ibus", "ēs", "ibus", "ēs");

    /**
     * The endings for the neuter forms.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Table<Case, Count, String> endingsNeuter = NounDeclensionUtil.endingsTable(null, "is", "ī", null, "ī", null,
                                                                                             "ia", "ium", "ibus", "ia", "ibus", "ia");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private INounDeclension() {}

    /**
     * Gets the only instance of this as specified by {@link cf.kayon.core.noun.NounDeclension}.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    @NotNull
    public static INounDeclension getInstance()
    {
        return INSTANCE;
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    @Override
    protected String selectCorrectEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        return gender == Gender.NEUTER ? endingsNeuter.get(caze, count) : endingsFeminine.get(caze, count);
    }

    // TODO decide to remove this - see <turris>
    //    @NotNull
    //    @Override
    //    public List<String> getPossibleForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    //    {
    //        // Accusative plural: Possibly
    //        if (caze == Case.ACCUSATIVE && count == Count.PLURAL)
    //        {
    //            ArrayList<String> buffer = new ArrayList<>(2);
    //            buffer.add(this.decline(caze, count, gender, rootWord));
    //            buffer.add(rootWord + "īs");
    //            return buffer;
    //        }
    //        return super.getPossibleForms(caze, count, gender, rootWord);
    //    }

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
