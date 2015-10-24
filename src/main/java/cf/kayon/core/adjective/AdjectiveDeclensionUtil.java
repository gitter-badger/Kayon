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

/**
 * Contains static utility methods around the usage and implementation of a {@link AdjectiveDeclension}.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class AdjectiveDeclensionUtil
{
    /**
     * The private constructor to never let anyone construct this static-only class.
     *
     * @since 0.0.1
     */
    private AdjectiveDeclensionUtil() {}

    /**
     * Universal endings for the comparative comparison degree.
     *
     * @since 0.0.1
     */
    @NotNull
    public static final Table<Gender, Count, Map<Case, String>> endingsComparative =
            AdjectiveDeclensionUtil.endingsTable("ior", "ioris", "iorī", "iorem", "iore", "ior", "iorēs", "iorum", "ioribus", "iorēs", "ioribus", "iorēs",
                                                 "ior", "ioris", "iorī", "iorem", "iore", "ior", "iorēs", "iorum", "ioribus", "iorēs", "ioribus", "iorēs",
                                                 "ius", "ioris", "iorī", "ius", "iore", "ius", "iora", "iorum", "ioribus", "iora", "ioribus", "iora");

    /**
     * Universal endings for the superlative comparison degree.
     *
     * @since 0.0.1
     */
    @NotNull
    public static final Table<Gender, Count, Map<Case, String>> endingsSuperlative =
            AdjectiveDeclensionUtil.endingsTable("us", "ī", "ō", "um", "ō", "e", "ī", "ōrum", "īs", "ōs", "īs", "ī",
                                                 "a", "ae", "ae", "am", "ā", "a", "ae", "ārum", "īs", "ās", "īs", "ae",
                                                 "um", "ī", "ō", "um", "ō", "um", "a", "ōrum", "īs", "a", "īs", "a");

    /**
     * Constructs a table of endings.
     * <p>
     * This method to prevent insane amounts of repetetive code. This method accepts the endings in the order
     * that the enums {@link Gender}, {@link Count} and {@link Count} appear, like this:
     * <ul>
     * <li>Nominative Singular Masculine</li>
     * <li>Genitive Singular Masculine</li>
     * <li>Dative Singular Masculine</li>
     * <li>Accusative Singular Masculine</li>
     * <li>Ablative Singular Masculine</li>
     * <li>Vocative Singular Masculine</li>
     * <li>Nominative Plural Masculine</li>
     * <li>Genitive Plural Masculine</li>
     * <li>Dative Plural Masculine</li>
     * <li>Accusative Plural Masculine</li>
     * <li>Ablative Plural Masculine</li>
     * <li>Vocative Plural Masculine</li>
     * <li>Nominative Singular Feminine</li>
     * </ul>
     * ...and so on.
     *
     * @param params The endings to put in the resulting table.
     * @return A {@link Tables#unmodifiableTable(Table) unmodifiable} table
     * (the maps are {@link Collections#unmodifiableMap(Map) unmodifiable} as well) of endings.
     * @throws NullPointerException             If the varchar argument is {@code null}.
     * @throws java.util.NoSuchElementException If there are too few parameters supplied.
     * @since 0.0.1
     */
    @NotNull
    public static Table<Gender, Count, Map<Case, String>> endingsTable(@NotNull String... params)
    {
        ObjectArrayIterator<String> it = new ObjectArrayIterator<>(params);
        Table<Gender, Count, Map<Case, String>> mainTable = HashBasedTable.create(3, 2);
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
}
