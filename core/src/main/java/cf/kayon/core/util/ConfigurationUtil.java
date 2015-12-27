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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static utilities around configuration.
 *
 * @author Ruben Anders
 * @since 0.2.0
 */
public class ConfigurationUtil
{
    /**
     * Converts a {@link Config}'s {@link Config#entrySet() entry set} to a {@link Properties} object.
     *
     * @param set The set to convert.
     * @param <K> The type of the entry set's keys.
     * @param <V> The type of the entry set's values.
     * @return A properties object.
     * @since 0.2.0
     */
    @NotNull
    public static <K, V extends ConfigValue> Properties toProperties(@NotNull Set<Map.Entry<K, V>> set)
    {
        Properties properties = new Properties();
        set.forEach(entry -> properties.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue().unwrapped())));
        return properties;
    }

    /**
     * Converts a map to a readable string, but does not print out values which belong to keys that, in their toString() representation,
     * are {@code password}.
     *
     * @param map         The map to convert.
     * @param mode        The mode of password showing. Set to {@code 0} to show passwords in plaintext, {@code 1} to show their hash with the specified algorithm and charset and
     *                    {@code 2} to just replace passwords with the {@code replacement} argument.
     * @param algorithm   Only required when mode is 1. The string specifying the hashing algorithm.
     * @param charsetName Only required when mode is 1. The charset name of the charset used to convert the password (String) to bytes for hashing.
     * @param replacement Only required when mode is 2. This is the replacement for all password values.
     * @return A readable string.
     * @throws NoSuchAlgorithmException     If the specified algorithm is not supported or does not exist.
     * @throws UnsupportedEncodingException If the specified encoding is not supported or does not exist.
     * @throws NullPointerException         If a required argument is {@code null}.
     * @throws IllegalArgumentException     If {@code mode} is not 0, 1 or 2.
     * @since 0.2.0
     */
    @NotNull
    public static String toStringPasswordAware(
            @NotNull Map<Object, Object> map, int mode, String algorithm, String charsetName, String replacement)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        switch (mode)
        {
            case 0:
                return map.toString();
            case 1:
                return toStringWithHashedPassword(map, algorithm, charsetName);
            case 2:
                return toStringWithReplacement(map, replacement);
        }
        throw new IllegalArgumentException("Illegal mode: Must be one of 0, 1 or 2!");
    }

    /**
     * Converts a map of Objects to a string, but not printing out any keys that translate into "password" in their toString() method.
     *
     * @param map         The map to convert to string.
     * @param algorithm   The hashing algorithm to use, if mode is set to 1.
     * @param charsetName The charset to use to convert the toString() value to bytes for hashing.
     * @return A string representing the map.
     * @throws NoSuchAlgorithmException     If the specified algorithm is not available.
     * @throws UnsupportedEncodingException If the specified encoding is not available.
     * @since 0.2.0
     */
    @NotNull
    public static String toStringWithHashedPassword(@NotNull Map<Object, Object> map, @NotNull String algorithm, @NotNull String charsetName)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        checkNotNull(map);
        checkNotEmpty(algorithm);
        checkNotEmpty(charsetName);

        StringBuilder builder = new StringBuilder("{");
        for (Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); )
        {
            Map.Entry<Object, Object> entry = iterator.next();
            String key = String.valueOf(entry.getKey());
            builder.append(key).append("=");
            if (Objects.equals(key, "password"))
            {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                byte[] bytes = entry.getValue().toString().getBytes(charsetName);

                builder.append("[").append(algorithm).append("[").append(charsetName).append("]]:");
                builder.append(Hex.encodeHexString(digest.digest(bytes)));
                builder.append("]");
            } else
                builder.append(entry.getValue());
            if (iterator.hasNext())
                builder.append(", ");
        }
        return builder.append("}").toString();
    }

    @NotNull
    public static String toStringWithReplacement(@NotNull Map<Object, Object> map, String replacement)
    {
        checkNotNull(map);
        checkNotEmpty(replacement);

        StringBuilder builder = new StringBuilder("{");
        for (Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); )
        {
            Map.Entry<Object, Object> entry = iterator.next();
            String key = String.valueOf(entry.getKey());
            builder.append(key).append("=").append(Objects.equals(key, "password") ? replacement : String.valueOf(entry.getValue()));
            if (iterator.hasNext())
                builder.append(", ");
        }
        return builder.append("}").toString();
    }

}
