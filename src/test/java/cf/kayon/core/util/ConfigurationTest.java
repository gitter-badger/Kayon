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

import cf.kayon.core.KayonContext;
import cf.kayon.core.TestContextUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigurationTest
{
    // Test defaults
    @Test
    public void testConfigurationDefaults()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        // database
        assertEquals("jdbc:h2:./database", context.getConfig().getString("database.url"));

        // database.info
        assertTrue(context.getConfig().getIsNull("database.info.user"));
        assertTrue(context.getConfig().getIsNull("database.info.password"));

        // database.log
        assertEquals(1, context.getConfig().getInt("database.log.mode"));
        assertEquals("SHA-256", context.getConfig().getString("database.log.algorithm"));
        assertEquals("utf8", context.getConfig().getString("database.log.charset"));
        assertEquals("(omitted)", context.getConfig().getString("database.log.replacement"));
    }
}
