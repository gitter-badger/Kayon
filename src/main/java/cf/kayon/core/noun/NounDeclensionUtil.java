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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import cf.kayon.core.Count;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NounDeclensionUtil
{

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

    public static <R, C, V> void putIfNotNull(@NotNull Table<R, C, V> tableToInsertInto, @NotNull R row, @NotNull C column, @Nullable V value)
    {
        if (value != null)
            tableToInsertInto.put(row, column, value);
    }


    @NotNull
    public static List<String> getPossibleForms(
            @NotNull NounDeclension nounDeclension, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
    {
        ArrayList<String> buffer = new ArrayList<>(1);
        buffer.add(nounDeclension.decline(caze, count, gender, rootWord));
        return buffer;
    }
}
