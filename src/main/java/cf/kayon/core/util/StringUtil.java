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

package cf.kayon.core.util;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtil
{

    @NotNull
    public static final Table<Boolean, Character, Character> specialCharsTable;

    @NotNull
    private static final List<ImmutablePair<Pattern, String>> patterns;

    static
    {
        @NotNull
        ArrayTable<Boolean, Character, Character> tbl = ArrayTable.create(() -> new ObjectArrayIterator<>(Boolean.TRUE, Boolean.FALSE),
                                                                          () -> new ObjectArrayIterator<>('A', 'a', 'E', 'e', 'I', 'i', 'O', 'o', 'U', 'u'));
        // Thanks to Commons Collections 4 for not being from the last century (no generics in 3.2.1 since java 1.2, *sigh*)

        tbl.put(true, 'A', 'Ā');
        tbl.put(true, 'a', 'ā');
        tbl.put(false, 'A', 'Ă');
        tbl.put(false, 'a', 'ă');

        tbl.put(true, 'E', 'Ē');
        tbl.put(true, 'e', 'ē');
        tbl.put(false, 'E', 'Ĕ');
        tbl.put(false, 'e', 'ĕ');

        tbl.put(true, 'I', 'Ī');
        tbl.put(true, 'i', 'ī');
        tbl.put(false, 'I', 'Ĭ');
        tbl.put(false, 'i', 'ĭ');

        tbl.put(true, 'O', 'Ō');
        tbl.put(true, 'o', 'ō');
        tbl.put(false, 'O', 'Ŏ');
        tbl.put(false, 'o', 'ŏ');

        tbl.put(true, 'U', 'Ū');
        tbl.put(true, 'u', 'ū');
        tbl.put(false, 'U', 'Ŭ');
        tbl.put(false, 'u', 'ŭ');

        specialCharsTable = Tables.unmodifiableTable(tbl);

        patterns = new ArrayList<>(5);
        patterns.add(new ImmutablePair<>(Pattern.compile("[ĀāĂă]"), "a"));
        patterns.add(new ImmutablePair<>(Pattern.compile("[ĒēĔĕ]"), "e"));
        patterns.add(new ImmutablePair<>(Pattern.compile("[ĪīĬĭ]"), "i"));
        patterns.add(new ImmutablePair<>(Pattern.compile("[ŌōŎŏ]"), "o"));
        patterns.add(new ImmutablePair<>(Pattern.compile("[ŪūŬŭ]"), "u"));
    }

    @NotNull
    @SuppressWarnings("ConstantConditions")
    public static <T extends CharSequence> T requireNonEmpty(@NotNull T csqToCheck)
    {
        if (csqToCheck == null)
            throw new NullPointerException();
        if (csqToCheck.length() == 0)
            throw new IllegalArgumentException();
        return csqToCheck;
    }

    @NotNull
    public static String removeVeryLast(@NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    {
        if (stringToRemoveFrom.endsWith(stringToRemove))
            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
        return stringToRemoveFrom;
    }

    @Nullable
    public static String removeVeryLastOrNull(@NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    {
        if (stringToRemoveFrom.endsWith(stringToRemove))
            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
        return null;
    }

    @NotNull
    public static String removeVeryLast(@NotNull String normalizedString, @NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    {
        if (normalizedString.endsWith(stringToRemove))
            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
        return stringToRemoveFrom;
    }

    @Nullable
    public static String removeVeryLastOrNull(@NotNull String normalizedString, @NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    {
        if (normalizedString.endsWith(stringToRemove))
            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
        return null;
    }

    /**
     * Applies a {@code Macron} or {@code Breve} to a character.
     *
     * @param doLengthen     {@code true} if a {@code Macron} should be applied, {@code false} if a {@code Breve} should be applied.
     * @param charToLengthen The character to apply the {@code Macron} or {@code Breve} to.
     * @return The character with applied {@code Macron} or {@code Breve}.
     * @throws IllegalArgumentException If the char could not be lengthened/shortened. This is thrown if the input {@code char} was not one of {@code A, a, E, e, I, i, O, o, U, u}
     *                                  (or a lengthened/shortened version of them).
     */
    public static char specialChar(boolean doLengthen, char charToLengthen) throws IllegalArgumentException
    {
        Character charOrNull = specialCharsTable.get(doLengthen, charToLengthen);
        if (charOrNull != null)
            return charOrNull;
        // "Expensive" part: Un-apply and apply again
        charOrNull = specialCharsTable.get(doLengthen, unSpecialChar(charToLengthen));
        if (charOrNull != null)
            return charOrNull;
        throw new IllegalArgumentException();
    }

    public static char unSpecialChar(char specialChar) throws IllegalArgumentException
    {
        if (specialCharsTable.contains(true, specialChar))
            return specialChar;
        for (Table.Cell<Boolean, Character, Character> currentCell : specialCharsTable.cellSet())
        {
            if (currentCell.getValue() == specialChar)
                return currentCell.getColumnKey();
        }
        throw new IllegalArgumentException();
    }

    public static String specialString(boolean doLengthen, char charToLengthen) throws IllegalArgumentException
    {
        return String.valueOf(specialChar(doLengthen, charToLengthen));
    }

    @NotNull
    public static String specialString(boolean doLengthen, @NotNull String stringToLengthen) throws IllegalArgumentException
    {
        StringBuilder stringBuilder = new StringBuilder(stringToLengthen.length());
        for (int i = 0; i < stringToLengthen.length(); i++)
        {
            try
            {
                stringBuilder.append(specialChar(doLengthen, stringToLengthen.charAt(i)));
            } catch (IllegalArgumentException e)
            {
                stringBuilder.append(stringToLengthen.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    @NotNull
    public static String unSpecialString(char specialChar)
    {
        return String.valueOf(unSpecialChar(specialChar));
    }

    @NotNull
    public static String unSpecialString(@NotNull String specialString)
    {
        StringBuilder stringBuilder = new StringBuilder(specialString.length());
        for (int i = 0; i < specialString.length(); i++)
        {
            try
            {
                stringBuilder.append(unSpecialChar(specialString.charAt(i)));
            } catch (IllegalArgumentException e)
            {
                stringBuilder.append(specialString.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    public static boolean normalizedEquals(@NotNull String first, @NotNull String second)
    {
        for (Pair<Pattern, String> currentPair : patterns)
        {
            first = currentPair.getLeft().matcher(first).replaceAll(currentPair.getRight());
            second = currentPair.getLeft().matcher(second).replaceAll(currentPair.getRight());
        }
        return first.equalsIgnoreCase(second);
    }
}
