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

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Provides static references about the Kayon application.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class KayonReference
{

    /**
     * The version as written in {@code /src/main/resources/version}.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final String VERSION;

    /**
     * The build ID as written in {@code /src/main/resources/build}.
     *
     * @since 0.0.1
     */
    private static final long BUILD;

    /**
     * Gets the version.
     *
     * @return The version string, as written in {@code /src/main/resources/version}.
     */
    @NotNull
    public static String getVersion()
    {
        return VERSION;
    }

    /**
     * Gets the build ID.
     *
     * @return The build ID as written in {@code /src/main/resources/build}.
     */
    public static long getBuild()
    {
        return BUILD;
    }

    static
    {
        try (InputStream inputStream = KayonReference.class.getResourceAsStream("/version");
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            VERSION = IOUtils.toString(inputStreamReader);
        } catch (IOException ioe)
        {
            throw new Error(ioe);
        }

        try (InputStream inputStream = KayonReference.class.getResourceAsStream("/build");
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            BUILD = Long.parseLong(IOUtils.toString(inputStreamReader));
        } catch (IOException ioe)
        {
            throw new Error(ioe);
        }
    }

}
