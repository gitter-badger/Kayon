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
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static utilities around strings.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
@Immutable
public class StringUtil
{

    /**
     * A private constructor that always fails to prevent instantiation.
     *
     * @throws IllegalStateException always
     * @since 0.2.3
     */
    private StringUtil()
    {
        throw new IllegalStateException();
    }

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
    public static final Table<Boolean, Character, Character> specialCharsTable = new ImmutableTable.Builder<Boolean, Character, Character>()
            .put(true, 'A', 'Ā')
            .put(true, 'a', 'ā')
            .put(false, 'A', 'Ă')
            .put(false, 'a', 'ă')
            .put(true, 'E', 'Ē')
            .put(true, 'e', 'ē')
            .put(false, 'E', 'Ĕ')
            .put(false, 'e', 'ĕ')
            .put(true, 'I', 'Ī')
            .put(true, 'i', 'ī')
            .put(false, 'I', 'Ĭ')
            .put(false, 'i', 'ĭ')
            .put(true, 'O', 'Ō')
            .put(true, 'o', 'ō')
            .put(false, 'O', 'Ŏ')
            .put(false, 'o', 'ŏ')
            .put(true, 'U', 'Ū')
            .put(true, 'u', 'ū')
            .put(false, 'U', 'Ŭ')
            .put(false, 'u', 'ŭ')
            .build();

    /**
     * Checks for three conditions on the specified {@link CharSequence}:
     * <ol>
     * <li>If {@code csqToCheck} is {@code null}, a {@link NullPointerException} without a detail message will be thrown.</li>
     * <li>If {@code csqToCheck} is empty ({@code csqToCheck.length() <= 0}), an {@link IllegalArgumentException} with the detail message
     * {@code "Empty string parameter"} will be thrown.</li>
     * <li>If {@code csqToCheck} is {@link StringUtils#isBlank(CharSequence) blank}, an {@link IllegalArgumentException} with the detail message
     * {@code "Blank string parameter"} will be thrown.</li>
     * </ol>
     *
     * @param csqToCheck The CharSquence to check.
     * @throws NullPointerException     If {@code csqToCheck} is {@code null}
     * @throws IllegalArgumentException If {@code csqToCheck} is empty ({@code csqToCheck.length() <= 0}) or {@link StringUtils#isBlank(CharSequence) blank}.
     * @since 0.0.1
     */
    @Contract(value = "null -> fail")
    public static void checkNotEmpty(CharSequence csqToCheck)
    {
        checkNotNull(csqToCheck);
        if (csqToCheck.length() <= 0)
            throw new IllegalArgumentException("Empty string parameter");
        if (StringUtils.isBlank(csqToCheck))
            throw new IllegalArgumentException("Blank string parameter");
    }

    /**
     * Removes the specified string {@code stringToRemove} from {@code stringToRemoveFrom} if {@code normalizedString} ends with {@code stringToRemove}.
     *
     * @param normalizedString   The string that needs to contain the {@code stringToRemove}.
     * @param stringToRemoveFrom The string that should be changed.
     * @param stringToRemove     The string to remove from {@code stringToRemoveFrom}.
     * @return {@code stringToRemoveFrom} without {@code stringToRemove} (if it was on the very last position). If the operation could not be completed
     * e.g. if {@code normalizedString} did not end in {@code stringToRemove}, {@code null} is returned.
     * @since 0.0.1
     */
    @Nullable
    public static String removeVeryLastOrNull(@NotNull String normalizedString, @NotNull String stringToRemoveFrom, @NotNull String stringToRemove)
    {
        if (normalizedString.endsWith(stringToRemove))
            return stringToRemoveFrom.substring(0, stringToRemoveFrom.length() - stringToRemove.length());
        return null;
    }

    /**
     * Returns the normal variant of a given char.
     *
     * @param specialChar The character, which is either lengthened, shortened or a normal char.
     * @return The normal version of the character.
     * @throws IllegalArgumentException If the character specified is not a special character or any of the possible normal variants of them.
     * @since 0.0.1
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
        throw new IllegalArgumentException("Character " + specialChar + " is not a special character or a normal variant of them!");
    }

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

    /**
     * Applies a all-special-form matching regular expression string. You can {@link java.util.regex.Pattern#compile(String) compile} the resulting string into a
     * {@link java.util.regex.Pattern Pattern} and use it
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
    public static String anySpecialRegex(@NotNull String form)
    {
        checkNotEmpty(form);
        StringBuilder patternBuilder = new StringBuilder(form.length() * 2);
        for (int i = 0; i < form.length(); i++)
        {
            final char current = form.charAt(i);
            switch (current)
            {
                case 'a':
                case 'ā':
                case 'ă':
                    patternBuilder.append("[aāă]");
                    break; // breaks switch, not loop
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
