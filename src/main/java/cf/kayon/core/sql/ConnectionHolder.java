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

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds the application database connection.
 * <p>
 * The connection is being closed automatically by a JVM Shutdown hook.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class ConnectionHolder
{
    /**
     * The connection.
     *
     * @since 0.0.1
     */
    private static Connection connection;

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                connection.close();
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }, "ConnectionHolder-closer"));
    }

    /**
     * Gets the connection instance.
     * <p>
     * The caller should be careful not to use the connection in a try-with-resources-statement or to close it.
     *
     * @return The connection. Never {@code null}.
     * @throws IllegalStateException If the connection has not been initialized yet.
     * @since 0.0.1
     */
    @NotNull
    public static Connection getConnection()
    {
        if (connection == null)
            throw new IllegalStateException("Connection has not been initialized yet!");
        return connection;
    }

    /**
     * Initializes the connection instance.
     *
     * @param newConnection The new connection.
     * @throws NullPointerException  If {@code newConnection} is {@code null}.
     * @throws IllegalStateException If the connection has already been initialized.
     * @since 0.0.1
     */
    public static void initializeConnection(@NotNull Connection newConnection)
    {
        checkNotNull(newConnection);
        if (connection != null)
            throw new IllegalStateException("Connection has already been initialized!");
        connection = newConnection;
    }
}
