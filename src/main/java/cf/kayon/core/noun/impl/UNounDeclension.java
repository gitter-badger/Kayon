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
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.NounDeclensionUtil;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class UNounDeclension extends StandardNounDeclension
{
    @NotNull
    private static final UNounDeclension INSTANCE = new UNounDeclension();

    @NotNull
    private final Table<Case, Count, String> endingsMasculineFeminine = NounDeclensionUtil.endingsTable("us", "ūs", "uī", "um", "ū", "us",
                                                                                                        "ūs", "uum", "ibus", "ūs", "ibus", "ūs");

    // Very, very rare
    @NotNull
    private final Table<Case, Count, String> endingsNeuter = NounDeclensionUtil.endingsTable("ū", "ūs", "u", "ū", "ū", "ū",
                                                                                             "ua", "uum", "ibus", "ua", "ibus", "ua");

    private UNounDeclension() {}

    @NotNull
    public static UNounDeclension getInstance()
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
        return gender == Gender.NEUTER ? endingsNeuter.get(caze, count) : endingsMasculineFeminine.get(caze, count);
    }

    @Nullable
    @Override
    public Gender getPrimaryGender()
    {
        return Gender.MASCULINE;
    }

    @Override
    public boolean allowsGender(@NotNull Gender genderToCheck)
    {
        checkNotNull(genderToCheck);
        return true;
    }
}
