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

import cf.kayon.core.CaseHandling;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static utilities around strings.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class StringUtil
{

    /**
     * A table holding lengthened and shortened characters.
     * <p>
     * Row: {@code true} if the character denoted by this cell is lengthened, {@code false} if it shortened.
     * <p>
     * Column: The 'normal' character, like {@code a}, {@code e}, etc...
     * <p>
     * Value: The lengthened/shortened character.
     *
     * @since 0.0.1
     */
    @NotNull
    public static final Table<Boolean, Character, Character> specialCharsTable;

    /**
     * A list of patterns to convert shortened/lengthened characters to their normal variants.
     * <p>
     * The key of the pair holds the pattern to apply, the value is the string to replace the pattern with.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final List<Pair<Pattern, String>> patterns;

    static
    {
        @NotNull
        ArrayTable<Boolean, Character, Character> tbl = ArrayTable.create(() -> new ObjectArrayIterator<>(Boolean.TRUE, Boolean.FALSE),
                                                                          () -> new ObjectArrayIterator<>('A', 'a', 'E', 'e', 'I', 'i', 'O', 'o', 'U', 'u'));

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

    @Contract(value = "null -> fail")
    public static void checkNotEmpty(CharSequence csqToCheck)
    {
        checkNotNull(csqToCheck);
        if (csqToCheck.length() == 0)
            throw new IllegalArgumentException();
    }

    //    @NotNull
    //    public static String removeVeryLast(@NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    //    {
    //        if (stringToRemoveFrom.endsWith(stringToRemove))
    //            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
    //        return stringToRemoveFrom;
    //    }
    //
    //    @Nullable
    //    public static String removeVeryLastOrNull(@NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    //    {
    //        if (stringToRemoveFrom.endsWith(stringToRemove))
    //            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
    //        return null;
    //    }
    //
    //    @NotNull
    //    public static String removeVeryLast(@NotNull String normalizedString, @NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    //    {
    //        if (normalizedString.endsWith(stringToRemove))
    //            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
    //        return stringToRemoveFrom;
    //    }

    @Nullable
    public static String removeVeryLastOrNull(@NotNull String normalizedString, @NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    {
        if (normalizedString.endsWith(stringToRemove))
            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
        return null;
    }

    //    /**
    //     * Applies a {@code Macron} or {@code Breve} to a character.
    //     *
    //     * @param doLengthen     {@code true} if a {@code Macron} should be applied, {@code false} if a {@code Breve} should be applied.
    //     * @param charToLengthen The character to apply the {@code Macron} or {@code Breve} to.
    //     * @return The character with applied {@code Macron} or {@code Breve}.
    //     * @throws IllegalArgumentException If the char could not be lengthened/shortened. This is thrown if the input {@code char} was not one of {@code A, a, E, e, I, i, O, o, U, u}
    //     *                                  (or a lengthened/shortened version of them).
    //     */
    //    public static char specialChar(boolean doLengthen, char charToLengthen) throws IllegalArgumentException
    //    {
    //        Character charOrNull = specialCharsTable.get(doLengthen, charToLengthen);
    //        if (charOrNull != null)
    //            return charOrNull;
    //        // "Expensive" part: Un-apply and apply again
    //        charOrNull = specialCharsTable.get(doLengthen, unSpecialChar(charToLengthen));
    //        if (charOrNull != null)
    //            return charOrNull;
    //        throw new IllegalArgumentException();
    //    }

    /**
     * Returns the normal variant of a given char.
     *
     * @param specialChar The character, which
     * @return The normal version of the character.
     * @throws IllegalArgumentException If the character specified is not a special character or any of the possible normal variants of them.
     */
    public static char unSpecialChar(char specialChar) throws IllegalArgumentException
    {
        if (specialCharsTable.get(true, specialChar) != null) // If table has the char as key, it's not special
            return specialChar;
        for (Table.Cell<Boolean, Character, Character> currentCell : specialCharsTable.cellSet())
        {
            if (currentCell.getValue() == specialChar)
                return currentCell.getColumnKey();
        }
        throw new IllegalArgumentException();
    }

    //    public static String specialString(boolean doLengthen, char charToLengthen) throws IllegalArgumentException
    //    {
    //        return String.valueOf(specialChar(doLengthen, charToLengthen));
    //    }

    //    @NotNull
    //    public static String specialString(boolean doLengthen, @NotNull String stringToLengthen) throws IllegalArgumentException
    //    {
    //        StringBuilder stringBuilder = new StringBuilder(stringToLengthen.length());
    //        for (int i = 0; i < stringToLengthen.length(); i++)
    //        {
    //            try
    //            {
    //                stringBuilder.append(specialChar(doLengthen, stringToLengthen.charAt(i)));
    //            } catch (IllegalArgumentException e)
    //            {
    //                stringBuilder.append(stringToLengthen.charAt(i));
    //            }
    //        }
    //        return stringBuilder.toString();
    //    }

    //    @NotNull
    //    public static String unSpecialString(char specialChar)
    //    {
    //        return String.valueOf(unSpecialChar(specialChar));
    //    }

    /**
     * Replaces all special characters in a string with their normal variants.
     *
     * @param specialString The string containing special characters.
     * @return A new string without special characters.
     * @since 0.0.1
     */
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

    //    public static boolean normalizedEquals(@NotNull String first, @NotNull String second)
    //    {
    //        for (Pair<Pattern, String> currentPair : patterns)
    //        {
    //            first = currentPair.getLeft().matcher(first).replaceAll(currentPair.getRight());
    //            second = currentPair.getLeft().matcher(second).replaceAll(currentPair.getRight());
    //        }
    //        return first.equalsIgnoreCase(second);
    //    }

    /**
     * Applies a all-special-form matching regular expression string. You can {@link Pattern#compile(String) compile} the resulting string into a {@link Pattern} and use it
     * to match any possible special variant of the supplied form.
     * <p>
     * Example:
     * <ul>
     * <li>{@code dominus} -&gt; {@code d[oōŏ]m[iīĭ]n[uūŭ]s}</li>
     * <li>{@code dominō} (notice the long {@code ō}) -&gt; {@code d[oōŏ]m[iīĭ]n[oōŏ]}</li>
     * <li>{@code ancilla} -&gt; {@code [aāă]nc[iīĭ]ll[aāă]}</li>
     * </ul>
     * <p>
     * This method does not handle uppercase characters (see annotation).
     * If you have a {@link String} with mixed lowercase and uppercase characters, call {@link String#toLowerCase() .toLowerCase()} on it first.
     *
     * @param form The form to replace all special-able chars with regex in.
     * @return A regular expression string matching any possible special form of the specified parameter. An empty string if the input is an empty string.
     * @throws NullPointerException If the specified {@code form} is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    public static String anySpecialRegex(String form)
    {
        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < form.length(); i++)
        {
            final char current = form.charAt(i);
            switch (current)
            {
                case 'a':
                case 'ā':
                case 'ă':
                    patternBuilder.append("[aāă]");
                    break;
                case 'e':
                case 'ē':
                case 'ĕ':
                    patternBuilder.append("[eēĕ]");
                    break;
                case 'i':
                case 'ī':
                case 'ĭ':
                    patternBuilder.append("[iīĭ]");
                    break;
                case 'o':
                case 'ō':
                case 'ŏ':
                    patternBuilder.append("[oōŏ]");
                    break;
                case 'u':
                case 'ū':
                case 'ŭ':
                    patternBuilder.append("[uūŭ]");
                    break;
                default:
                    patternBuilder.append(current);
                    break;
            }
        }
        return patternBuilder.toString();
    }
}
