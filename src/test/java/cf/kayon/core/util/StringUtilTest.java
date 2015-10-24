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

import org.junit.Test;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.assertEquals;

public class StringUtilTest
{
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    public void testRequireNonEmpty()
    {
        exceptionThrownBy(() -> StringUtil.requireNonEmpty(null), NullPointerException.class);
        exceptionThrownBy(() -> StringUtil.requireNonEmpty(""), IllegalArgumentException.class);
        StringUtil.requireNonEmpty("Valid");
    }

    @Test
    public void testAnySpecialRegex()
    {
        assertEquals("d[oōŏ]m[iīĭ]n[uūŭ]s", StringUtil.anySpecialRegex("dominus"));
        assertEquals("d[oōŏ]m[iīĭ]n[oōŏ]", StringUtil.anySpecialRegex("dominō"));
        assertEquals("[aāă]nc[iīĭ]ll[aāă]", StringUtil.anySpecialRegex("ancilla"));
    }
}
