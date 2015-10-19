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

import org.junit.Test;

public class DatabaseDriverTest
{

    //    @Test
    //    public void testHSQLDB() throws ClassNotFoundException
    //    {
    //        Class.forName("org.hsqldb.jdbc.JDBCDriver");
    //    }

    //    @Test
    //    public void testH2() throws ClassNotFoundException
    //    {
    //        Class.forName("org.h2.Driver");
    //    }

    @Test
    public void testSQLite() throws ClassNotFoundException
    {
        Class.forName("org.sqlite.JDBC");
    }
}
