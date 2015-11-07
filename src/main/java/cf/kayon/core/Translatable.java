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

package cf.kayon.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * Describes something translatable with dynamic translations that can be modified at runtime.
 * <p>
 * Major incompatible changes as of 0.2.0.
 *
 * @author Ruben Anders
 * @see Locale
 * @since 0.0.1
 */
public interface Translatable
{
    /**
     * Gets the translations of this translatable.
     * <p>
     * May not return {@code null}, but may be empty.
     *
     * @return The translations.
     * @since 0.2.0
     */
    @NotNull
    Map<Locale, String> getTranslations();

    /**
     * Gets a translation string.
     * <p>
     * This method is supposed to fall back on other alternate locales of same language, if necessary.
     *
     * @param locale The locale to translate to.
     * @return A translation. {@code null} if no translation could be found.
     * @throws NullPointerException If {@code locale} is {@code null}.
     * @since 0.2.0
     */
    @Nullable
    String getTranslation(@NotNull Locale locale);

    /**
     * Sets the translations of this Translatable.
     * <p>
     * This method is used by deserialization.
     *
     * @param map The map to set.
     * @throws NullPointerException If {@code map} is {@code null}.
     * @since 0.2.0
     */
    void setTranslations(@NotNull Map<Locale, String> map);

}
