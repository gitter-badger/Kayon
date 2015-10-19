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

package cf.kayon.core.adjective;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AdjectiveDeclensionUtil
{
    private AdjectiveDeclensionUtil() {}

    @NotNull
    public static final Table<Gender, Count, Map<Case, String>> endingsComparative =
            AdjectiveDeclensionUtil.endingsTable("ior", "ioris", "iorī", "iorem", "iore", "ior", "iorēs", "iorum", "ioribus", "iorēs", "ioribus", "iorēs",
                                                 "ior", "ioris", "iorī", "iorem", "iore", "ior", "iorēs", "iorum", "ioribus", "iorēs", "ioribus", "iorēs",
                                                 "ius", "ioris", "iorī", "ius", "iore", "ius", "iora", "iorum", "ioribus", "iora", "ioribus", "iora");

    @NotNull
    public static final Table<Gender, Count, Map<Case, String>> endingsSuperlative =
            AdjectiveDeclensionUtil.endingsTable("us", "ī", "ō", "um", "ō", "e", "ī", "ōrum", "īs", "ōs", "īs", "ī",
                                                 "a", "ae", "ae", "am", "ā", "a", "ae", "ārum", "īs", "ās", "īs", "ae",
                                                 "um", "ī", "ō", "um", "ō", "um", "a", "ōrum", "īs", "a", "īs", "a");
    //    @NotNull
    //    public static final Set<AdjectiveForm> NEUTER_PREPARED_EQUAL_FORMS_POSITIVE_SINGULAR =
    //            ImmutableSet.of(new AdjectiveForm(ComparisonDegree.POSITIVE, Case.NOMINATIVE, Count.SINGULAR, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.POSITIVE, Case.ACCUSATIVE, Count.SINGULAR, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.POSITIVE, Case.VOCATIVE, Count.SINGULAR, Gender.NEUTER));
    //
    //    @NotNull
    //    public static final Set<AdjectiveForm> NEUTER_PREPARED_EQUAL_FORMS_POSITIVE_PLURAL =
    //            ImmutableSet.of(new AdjectiveForm(ComparisonDegree.POSITIVE, Case.NOMINATIVE, Count.PLURAL, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.POSITIVE, Case.ACCUSATIVE, Count.PLURAL, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.POSITIVE, Case.VOCATIVE, Count.PLURAL, Gender.NEUTER));
    //
    //    @NotNull
    //    public static final Set<AdjectiveForm> NEUTER_PREPARED_EQUAL_FORMS_COMPARATIVE_SINGULAR =
    //            ImmutableSet.of(new AdjectiveForm(ComparisonDegree.COMPARATIVE, Case.NOMINATIVE, Count.SINGULAR, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.COMPARATIVE, Case.ACCUSATIVE, Count.SINGULAR, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.COMPARATIVE, Case.VOCATIVE, Count.SINGULAR, Gender.NEUTER));
    //
    //    @NotNull
    //    public static final Set<AdjectiveForm> NEUTER_PREPARED_EQUAL_FORMS_COMPARATIVE_PLURAL =
    //            ImmutableSet.of(new AdjectiveForm(ComparisonDegree.COMPARATIVE, Case.NOMINATIVE, Count.PLURAL, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.COMPARATIVE, Case.ACCUSATIVE, Count.PLURAL, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.COMPARATIVE, Case.VOCATIVE, Count.PLURAL, Gender.NEUTER));
    //
    //    @NotNull
    //    public static final Set<AdjectiveForm> NEUTER_PREPARED_EQUAL_FORMS_SUPERLATIVE_SINGULAR =
    //            ImmutableSet.of(new AdjectiveForm(ComparisonDegree.SUPERLATIVE, Case.NOMINATIVE, Count.SINGULAR, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.SUPERLATIVE, Case.ACCUSATIVE, Count.SINGULAR, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.SUPERLATIVE, Case.VOCATIVE, Count.SINGULAR, Gender.NEUTER));
    //
    //    @NotNull
    //    public static final Set<AdjectiveForm> NEUTER_PREPARED_EQUAL_FORMS_SUPERLATIVE_PLURAL =
    //            ImmutableSet.of(new AdjectiveForm(ComparisonDegree.SUPERLATIVE, Case.NOMINATIVE, Count.PLURAL, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.SUPERLATIVE, Case.ACCUSATIVE, Count.PLURAL, Gender.NEUTER),
    //                            new AdjectiveForm(ComparisonDegree.SUPERLATIVE, Case.VOCATIVE, Count.PLURAL, Gender.NEUTER));
    //
    //    @NotNull
    //    public static Set<AdjectiveForm> getPreparedEqualNeuterForms(@NotNull ComparisonDegree comparisonDegree, @NotNull Count count)
    //    {
    //        checkNotNull(comparisonDegree);
    //        checkNotNull(count);
    //        switch (comparisonDegree)
    //        {
    //            case POSITIVE:
    //                return count == Count.SINGULAR ? NEUTER_PREPARED_EQUAL_FORMS_POSITIVE_SINGULAR : NEUTER_PREPARED_EQUAL_FORMS_POSITIVE_PLURAL;
    //            case COMPARATIVE:
    //                return count == Count.SINGULAR ? NEUTER_PREPARED_EQUAL_FORMS_COMPARATIVE_SINGULAR : NEUTER_PREPARED_EQUAL_FORMS_COMPARATIVE_PLURAL;
    //            case SUPERLATIVE:
    //                return count == Count.SINGULAR ? NEUTER_PREPARED_EQUAL_FORMS_SUPERLATIVE_SINGULAR : NEUTER_PREPARED_EQUAL_FORMS_SUPERLATIVE_PLURAL;
    //        }
    //        return null;
    //    }

    // NomSgM, GenSgM, DatSgM, AccSgM, AblSgM, VocSgM, NomPlM, [pl...], NomSgF, [...], NomSgN, [...]
    @NotNull
    public static Table<Gender, Count, Map<Case, String>> endingsTable(@NotNull String... params)
    {
        Table<Gender, Count, Map<Case, String>> mainTable = HashBasedTable.create(3, 2);
        ObjectArrayIterator<String> it = new ObjectArrayIterator<>(params);
        for (Gender gender : Gender.values())
            for (Count count : Count.values())
            {
                Map<Case, String> subMap = new HashMap<>(6);
                mainTable.put(gender, count, Collections.unmodifiableMap(subMap)); // Unmodifiable view
                for (Case caze : Case.values())
                {
                    @Nullable
                    String next = it.next();
                    subMap.put(caze, next);
                }
            }
        return Tables.unmodifiableTable(mainTable);
    }

    //    public static Set<AdjectiveForm> applyVocativeEquals(
    //            @NotNull Set<AdjectiveForm> setToAddTo, boolean doApply,
    //            @NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull AdjectiveDeclension recursiveTarget)
    //    {
    //        checkNotNull(setToAddTo);
    //        checkNotNull(comparisonDegree);
    //        checkNotNull(caze);
    //        checkNotNull(count);
    //        checkNotNull(gender);
    //
    //        if (doApply && (caze == Case.NOMINATIVE || caze == Case.VOCATIVE))
    //        {
    //            setToAddTo.add(new AdjectiveForm(comparisonDegree, Case.NOMINATIVE, count, gender));
    //            setToAddTo.add(new AdjectiveForm(comparisonDegree, Case.VOCATIVE, count, gender));
    //        }
    //        return setToAddTo;
    //    }
}
