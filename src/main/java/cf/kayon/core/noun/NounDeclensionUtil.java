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

package cf.kayon.core.noun;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static utilities for {@link NounDeclension}s.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class NounDeclensionUtil
{

    /**
     * Constructs a {@link Tables#unmodifiableTable(Table) unmodifiable} table of endings.
     *
     * @param NomSg The nominative singular form.
     * @param GenSg The genitive singular form.
     * @param DatSg The dative singular form.
     * @param AccSg The accusative singular form.
     * @param AblSg The ablative singular form.
     * @param VocSg The vocative singular form.
     * @param NomPl The nominative  plural form.
     * @param GenPl The genitive plural form.
     * @param DatPl The dative plural form.
     * @param AccPl The accusative plural form.
     * @param AblPl The ablative plural form.
     * @param VocPl The vocative plural form.
     * @return A table of endings.
     * @since 0.0.1
     */
    @NotNull
    public static Table<Case, Count, String> endingsTable(
            @Nullable String NomSg,
            @Nullable String GenSg,
            @Nullable String DatSg,
            @Nullable String AccSg,
            @Nullable String AblSg,
            @Nullable String VocSg,
            @Nullable String NomPl,
            @Nullable String GenPl,
            @Nullable String DatPl, @Nullable String AccPl, @Nullable String AblPl, @Nullable String VocPl)
    {
        @NotNull
        HashBasedTable<Case, Count, String> tbl = HashBasedTable.create(6, 2);

        putIfNotNull(tbl, Case.NOMINATIVE, Count.SINGULAR, NomSg);
        putIfNotNull(tbl, Case.GENITIVE, Count.SINGULAR, GenSg);
        putIfNotNull(tbl, Case.DATIVE, Count.SINGULAR, DatSg);
        putIfNotNull(tbl, Case.ACCUSATIVE, Count.SINGULAR, AccSg);
        putIfNotNull(tbl, Case.ABLATIVE, Count.SINGULAR, AblSg);
        putIfNotNull(tbl, Case.VOCATIVE, Count.SINGULAR, VocSg);

        putIfNotNull(tbl, Case.NOMINATIVE, Count.PLURAL, NomPl);
        putIfNotNull(tbl, Case.GENITIVE, Count.PLURAL, GenPl);
        putIfNotNull(tbl, Case.DATIVE, Count.PLURAL, DatPl);
        putIfNotNull(tbl, Case.ACCUSATIVE, Count.PLURAL, AccPl);
        putIfNotNull(tbl, Case.ABLATIVE, Count.PLURAL, AblPl);
        putIfNotNull(tbl, Case.VOCATIVE, Count.PLURAL, VocPl);

        return Tables.unmodifiableTable(tbl);
    }

    /**
     * Puts a entry into a table, if the value is not {@code null}.
     *
     * @param tableToInsertInto The mutable table to insert into.
     * @param row               The row key.
     * @param column            The column key.
     * @param value             The value.
     * @param <R>               The type of the row key.
     * @param <C>               The type of the column key.
     * @param <V>               The type of the value.
     * @throws NullPointerException If {@code tableToInsertInto}, {@code row} or {@code column} is {@code null}
     * @since 0.0.1
     */
    public static <R, C, V> void putIfNotNull(@NotNull Table<R, C, V> tableToInsertInto, @NotNull R row, @NotNull C column, @Nullable V value)
    {
        checkNotNull(tableToInsertInto);
        checkNotNull(row);
        checkNotNull(column);
        if (value != null)
            tableToInsertInto.put(row, column, value);
    }

    //    @NotNull
    //    public static List<String> getPossibleForms(
    //            @NotNull NounDeclension nounDeclension, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    //    {
    //        ArrayList<String> buffer = new ArrayList<>(1);
    //        buffer.add(nounDeclension.decline(caze, count, gender, rootWord));
    //        return buffer;
    //    }

    /**
     * Reflectively reconstructs a NounDeclension by invoking its {@code public static <theClass> getInstance()}
     *
     * @param className The name of the class.
     * @return A NounDeclension. {@code null} if the reconstruction was not successful or the class name was {@code null}.
     * @since 0.0.1
     */
    @Nullable
    @Contract("null -> null")
    public static NounDeclension forName(@Nullable String className)
    {
        if (className == null)
            return null;
        try
        {
            Class<?> clazz = Class.forName(className);
            Method m = clazz.getMethod("getInstance");
            return (NounDeclension) m.invoke(null);
        } catch (Exception e)
        {
            return null;
        }
    }
}
