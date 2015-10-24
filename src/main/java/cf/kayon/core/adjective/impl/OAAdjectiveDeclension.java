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
import cf.kayon.core.CaseHandling;
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

/**
 * The implementation for the first/second adjective declension, nominative singular masculine positive ending in -us.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class OAAdjectiveDeclension extends StandardAdjectiveDeclension
{

    /**
     * The only instance of this singleton.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final OAAdjectiveDeclension INSTANCE = new OAAdjectiveDeclension();

    /**
     * The endings for the positive forms.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Table<Gender, Count, Map<Case, String>> positiveEndingsTable =
            AdjectiveDeclensionUtil.endingsTable("us", "ī", "ō", "um", "ō", "e", "ī", "ōrum", "īs", "ōs", "īs", "ī",
                                                 "a", "ae", "ae", "am", "ā", "a", "ae", "ārum", "īs", "ās", "īs", "ae",
                                                 "um", "ī", "ō", "um", "ō", "um", "a", "ōrum", "īs", "a", "īs", "a");

    /**
     * The private constructor to never let anybody construct this class.
     *
     * @since 0.0.1
     */
    private OAAdjectiveDeclension() {}

    /**
     * Gets the only instance of this OAAdjectiveDeclension.
     *
     * @return The only instance.
     * @since 0.0.1
     */
    @NotNull
    public static OAAdjectiveDeclension getInstance()
    {
        return INSTANCE;
    }

    /**
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    @Override
    protected String selectCorrectPositiveEndingOrNull(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return positiveEndingsTable.get(gender, count).get(caze);
    }

    /**
     * @since 0.0.1
     */
    @Nullable
    @Override
    protected Set<AdjectiveForm> getEqualFormsPositive(@NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        return null;
    }

    /**
     * @since 0.0.1
     */
    @Override
    protected boolean applyPositiveVocativeEquals(@NotNull Count count)
    {
        checkNotNull(count);
        return count == Count.PLURAL;
    }
}
