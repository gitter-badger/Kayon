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

import java.util.Map;

/**
 * Describes something translatable with dynamic translations that can be modified at runtime.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public interface Translatable
{

    // https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    @NotNull
    Map<String, String> getTranslations();

}
