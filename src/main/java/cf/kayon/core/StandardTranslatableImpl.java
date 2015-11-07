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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides anstandard implementation of a {@link Translatable}.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class StandardTranslatableImpl implements Translatable
{
    /**
     * The backing map.
     *
     * @since 0.2.0
     */
    private Map<Locale, String> translations = new HashMap<>();

    /**
     * @since 0.2.0
     */
    @NotNull
    @Override
    public Map<Locale, String> getTranslations()
    {
        return translations;
    }

    /**
     * @since 0.2.0
     */
    @Nullable
    @Override
    public String getTranslation(@NotNull Locale locale)
    {
        String strictTranslation = translations.get(locale);
        if (strictTranslation != null) return strictTranslation;

        return translations.get(new Locale(locale.getLanguage()));
    }

    /**
     * @since 0.2.0
     */
    @Override
    public void setTranslations(@NotNull Map<Locale, String> map)
    {
        checkNotNull(map);
        this.translations = map;
    }
}
