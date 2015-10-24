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

package cf.kayon.core.sql;

import cf.kayon.core.Gender;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Provides static utilities around database actions.
 *
 * @since 0.0.1
 */
public class SQLUtil
{
    /**
     * Gets the ID of a gender.
     * <p>
     * The ID is the enum ordinal (defined by the order in which the constants have been declared).
     *
     * @param gender The gender to get the ID for.
     * @return A {@code byte} representing the ID.
     * @since 0.0.1
     */
    public static byte idForGender(Gender gender)
    {
        return (byte) ArrayUtils.indexOf(Gender.values(), gender);
    }

    /**
     * Gets a gender for a ID.
     * <p>
     * The ID is the enum ordinal (defined by the order in which the constants have been declared).
     *
     * @param id The ID.
     * @return The gender.
     * @throws ArrayIndexOutOfBoundsException If the ID does not represent a gender.
     * @since 0.0.1
     */
    @NotNull
    public static Gender genderForId(byte id)
    {
        return Gender.values()[id];
    }

}
