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

import cf.kayon.core.sql.NounSQLFactory;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.typesafe.config.Config;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable class providing a application execution context to {@link Contexed} objects.
 *
 * @author Ruben Anders
 * @since 0.2.0
 */
public class KayonContext
{
    /**
     * Provides a static final Pattern for matching newline characters (CR and LF).
     *
     * @since 0.2.3
     */
    @NotNull
    private static final Pattern PATTERN_NEWLINES = Pattern.compile("[\n\r]");

    /**
     * The connection.
     *
     * @since 0.2.0
     */
    @NotNull
    private final Connection connection;

    /**
     * The config.
     *
     * @since 0.2.0
     */
    @NotNull
    private final Config config;

    /**
     * The Noun SQL Factory.
     *
     * @since 0.2.0
     */
    @NotNull
    private final NounSQLFactory nounSQLFactory;

    /**
     * The version as written in {@code /src/main/resources/version}.
     *
     * @since 0.2.0
     */
    @NotNull
    private final String version;

    /**
     * The build ID as written in {@code /src/main/resources/build}.
     *
     * @since 0.2.0
     */
    private final long build;

    // since 0.2.0
    {
        //noinspection HardcodedFileSeparator
        try (InputStream inputStream = this.getClass().getResourceAsStream("/version");
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            version = PATTERN_NEWLINES.matcher(IOUtils.toString(inputStreamReader)).replaceAll("");
        } catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }

        //noinspection HardcodedFileSeparator
        try (InputStream inputStream = this.getClass().getResourceAsStream("/build");
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            build = Long.parseLong(PATTERN_NEWLINES.matcher(IOUtils.toString(inputStreamReader)).replaceAll(""));
        } catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        } catch (NumberFormatException e)
        {
            throw new RuntimeException("Build number in file is in invalid format", e);
        }
    }

    /*
     * Thread safety notice
     *
     * All set fields are final, guaranteeing memory visibility.
     */
    public KayonContext(@NotNull Connection connection, @NotNull Config config)
    {
        checkNotNull(connection);
        checkNotNull(config);
        this.connection = connection;
        this.config = config;
        this.nounSQLFactory = new NounSQLFactory(this);
    }

    /**
     * Gets the build ID.
     *
     * @return The build ID as written in {@code /src/main/resources/build}.
     * @since 0.2.0
     */
    public long getBuild()
    {
        return build;
    }

    /**
     * Gets the version.
     *
     * @return The version string, as written in {@code /src/main/resources/version}.
     * @since 0.2.0
     */
    @NotNull
    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("connection", connection)
                          .add("config", config)
                          .add("nounSQLFactory", nounSQLFactory)
                          .add("version", version)
                          .add("build", build)
                          .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof KayonContext)) return false;
        KayonContext context = (KayonContext) o;
        return build == context.build &&
               Objects.equal(connection, context.connection) &&
               Objects.equal(config, context.config) &&
               Objects.equal(nounSQLFactory, context.nounSQLFactory) &&
               Objects.equal(version, context.version);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(connection, config, nounSQLFactory, version, build);
    }

    @NotNull
    public Config getConfig()
    {
        return config;
    }

    @NotNull
    public Connection getConnection()
    {
        return connection;
    }

    @NotNull
    public NounSQLFactory getNounSQLFactory()
    {
        return nounSQLFactory;
    }
}
