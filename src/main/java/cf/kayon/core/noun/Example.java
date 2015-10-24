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

import java.util.HashMap;
import java.util.Locale;

public class Example
{
    private final HashMap<Locale, String> translationsMap = new HashMap<>();

    /*
     * +------------------------+-------------------+-------------------+
     * |         Input          |  Expected output  |   Actual output   |
     * +------------------------+-------------------+-------------------+
     * | new Locale("en")       | "enTranslation"   | "enTranslation"   |
     * | new Locale("en", "CA") | "enTranslation"   | null              | <-- Did not fall back
     * | new Locale("de")       | "deTranslation"   | "deTranslation"   |
     * | new Locale("de", "DE") | "deTranslation"   | null              | <-- Did not fall back
     * | new Locale("de", "AT") | "deATTranslation" | "deATTranslation" |
     * | new Locale("fr")       | "frTranslation"   | "frTranslation"   |
     * | new Locale("fr", "CA") | "frTranslation"   | null              | <-- Did not fall back
     * +------------------------+-------------------+-------------------+
     */
    public String getTranslation(Locale locale)
    {
        return translationsMap.get(locale);
    }

    public void addTranslation(Locale locale, String translation)
    {
        translationsMap.put(locale, translation);
    }

    // dynamic class initializer
    {
        addTranslation(new Locale("en"), "enTranslation");
        addTranslation(new Locale("de"), "deTranslation");
        addTranslation(new Locale("fr"), "frTranslation");
        addTranslation(new Locale("de", "AT"), "deATTranslation");
    }
}
