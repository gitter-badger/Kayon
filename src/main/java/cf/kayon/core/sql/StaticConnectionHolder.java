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

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class StaticConnectionHolder
{
    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(StaticConnectionHolder::close, "StaticConnectionHolder-closer"));
    }

    private static final Map<String, Connection> connections = Maps.newHashMap();

    @Nullable
    public static Connection connectionForId(@NotNull String id)
    {
        checkNotNull(id);
        return connections.get(id);
    }

    public static void registerNewConnection(@NotNull String id, @NotNull Connection connection)
    {
        checkNotNull(id);
        checkNotNull(connection);
        connections.put(id, connection);
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    public static void close()
    {
        for (Map.Entry<String, Connection> connectionEntry : connections.entrySet())
        {
            try
            {
                Connection connection = connectionEntry.getValue();
                if (connection != null)
                    connection.close();
            } catch (SQLException ignored) {} // .close() could throw and prevent all following connections from closing.
        }
    }
}
