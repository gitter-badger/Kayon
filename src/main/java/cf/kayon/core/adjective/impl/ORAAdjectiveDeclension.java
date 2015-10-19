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

package cf.kayon.core.adjective.impl;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.adjective.AdjectiveDeclensionUtil;
import cf.kayon.core.adjective.AdjectiveForm;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ORAAdjectiveDeclension extends StandardAdjectiveDeclension
{
    @NotNull
    private static final ORAAdjectiveDeclension INSTANCE = new ORAAdjectiveDeclension();
    @NotNull
    private final Table<Gender, Count, Map<Case, String>> positiveEndingsTable =
            AdjectiveDeclensionUtil.endingsTable(null, "ī", "ō", "um", "ō", null, "ī", "ōrum", "īs", "ōs", "īs", "ī",
                                                 "a", "ae", "ae", "am", "ā", "a", "ae", "ārum", "īs", "ās", "īs", "ae",
                                                 "um", "ī", "ō", "um", "ō", "um", "a", "ōrum", "īs", "a", "īs", "a");

    private ORAAdjectiveDeclension() {}

    @NotNull
    public static ORAAdjectiveDeclension getInstance()
    {
        return INSTANCE;
    }

    @Nullable
    @Override
    protected String selectCorrectPositiveEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return positiveEndingsTable.get(gender, count).get(caze);
    }

    @Nullable
    @Override
    protected Set<AdjectiveForm> getEqualFormsPositive(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return null;
    }

    @Override
    protected boolean applyVocativeEquals(@NotNull Count count)
    {
        checkNotNull(count);
        return true;
    }
}
