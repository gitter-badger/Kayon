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
import cf.kayon.core.util.StringUtil;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class ORNounDeclension extends StandardNounDeclension
{
    @NotNull
    private static final ORNounDeclension INSTANCE = new ORNounDeclension();
    @NotNull
    private final Table<Case, Count, String> endings = NounDeclensionUtil.endingsTable(null, "ī", "ō", "um", "ō", null,
                                                                                       "ī", "ōrum", "īs", "ōs", "īs", "ī");

    private ORNounDeclension() {}

    @NotNull
    public static ORNounDeclension getInstance()
    {
        return INSTANCE;
    }

    @Nullable
    @Override
    public Gender getPrimaryGender()
    {
        return Gender.MASCULINE;
    }

//    @Nullable
//    @Override
//    public List<String> getAlternateForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//        checkNotNull(gender);
//        checkNotNull(rootWord);
//
//        if (caze == Case.GENITIVE && count == Count.SINGULAR &&
//            StringUtil.unSpecialString(rootWord).endsWith("i")) // Most expensive check last (short-circuiting statement)
//        {
//            ArrayList<String> buffer = new ArrayList<>(4);
//            buffer.add(rootWord + "iī");
//            return buffer;
//        }
//        return null;
//    }
//
//    @Override
//    public boolean hasAlternateForms(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
//    {
//        return caze == Case.GENITIVE && count == Count.SINGULAR && StringUtil.unSpecialString(rootWord).endsWith("i");
//    }

    @NotNull
    @Override
    public String decline(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
            throws FormingException
    {
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        checkNotNull(rootWord);

        if (caze == Case.VOCATIVE && count == Count.SINGULAR &&
            StringUtil.unSpecialString(rootWord).endsWith("i")) // Most expensive check last (short-circuiting statement)
            return rootWord;

        return rootWord + this.selectCorrectEnding(caze, count, gender);
    }

    @NotNull
    @Override
    public String determineRootWord(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
    {
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        checkNotNull(declinedForm);

        if (caze == Case.VOCATIVE && count == Count.SINGULAR &&
            StringUtil.unSpecialString(declinedForm).endsWith("i")) // Most expensive check last (short-circuiting statement)
            return declinedForm;
        return FormingUtil.determineRootWord(declinedForm, this.selectCorrectEnding(caze, count, gender));
    }

    @Override
    public boolean allowsGender(@NotNull Gender genderToCheck)
    {
        return checkNotNull(genderToCheck) != Gender.NEUTER;
    }

    @Nullable
    @Override
    protected String selectCorrectEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        return endings.get(caze, count);
    }
}
