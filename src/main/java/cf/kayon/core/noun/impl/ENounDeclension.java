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

import com.google.common.collect.Table;
import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclensionUtil;
import cf.kayon.core.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class ENounDeclension extends StandardNounDeclension
{
    private static final ENounDeclension INSTANCE = new ENounDeclension();
    private final Table<Case, Count, String> endings = NounDeclensionUtil.endingsTable("ēs", "eī", "eī", "em", "ē", "ēs",
                                                                                       "ēs", "ērum", "ēbus", "ēs", "ēbus", "ēs");

    private ENounDeclension() {}

    public static ENounDeclension getInstance()
    {
        return INSTANCE;
    }

    @Nullable
    @Override
    protected String selectCorrectEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        if (count == Count.SINGULAR && (caze == Case.GENITIVE || caze == Case.DATIVE))
            return "ēī";
        return endings.get(caze, count);
    }

    @NotNull
    @Override
    public String decline(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        checkNotNull(rootWord);
        if (count == Count.SINGULAR && (caze == Case.GENITIVE || caze == Case.DATIVE) &&
            StringUtil.unSpecialString(rootWord).endsWith("i")) // Most expensive check last
            return rootWord + "ēī";
        return rootWord + selectCorrectEnding(caze, count, gender);
    }

    @Nullable
    @Override
    public Gender getPrimaryGender()
    {
        return Gender.FEMININE;
    }

    @Override
    public boolean allowsGender(@NotNull Gender genderToCheck)
    {
        return checkNotNull(genderToCheck) != Gender.NEUTER;
    }
}
