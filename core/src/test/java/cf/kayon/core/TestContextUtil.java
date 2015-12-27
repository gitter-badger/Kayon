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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class TestContextUtil
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(TestContextUtil.class);

    @NotNull
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @NotNull
    public static KayonContext newTestingContext()
    {
        final int index = COUNTER.getAndIncrement();
        LOGGER.info("Creating context #" + index + " for caller method " + new Throwable().getStackTrace()[1]);

        Config config = ConfigFactory.load(); // do not load from file(s) outside
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:" + index);
            KayonContext context = new KayonContext(connection, config);

            context.getNounSQLFactory().setupDatabaseForNouns();
            context.getNounSQLFactory().compileStatements();

            return context;
        } catch (SQLException e)
        {
            LOGGER.error("Could not create a new testing context!", e);
            throw new RuntimeException(e);
        }
    }

    public static void closeContext(@Nullable KayonContext context)
    {
        if (context != null)
        {
            try
            {
                context.getConnection().close();
            } catch (SQLException e)
            {
                throw new RuntimeException(e); // Make the test fail
            }
        }
    }
}
