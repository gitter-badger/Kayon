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

import java.lang.annotation.*;

/**
 * Indicates how a method supports uppercase or lowercase characters.
 * <p>
 * Passing a method annotated as {@link cf.kayon.core.CaseHandling.CaseType#LOWERCASE_ONLY} uppercase characters may lead to unexpected results
 * (and the other way around for {@link cf.kayon.core.CaseHandling.CaseType#UPPERCASE_ONLY}.
 * <p>
 * This behaviour is implemented to save performance, since all latin forms are case-insensitive.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface CaseHandling
{
    /**
     * How the annotated method or constructor handles characters of different cases.
     *
     * @return A CaseType.
     */
    CaseType value();

    /**
     * Describes one of the three possibilities how code can handle uppercase or lowercase characters.
     *
     * @author Ruben Anders
     * @since 0.0.1
     */
    enum CaseType
    {
        /**
         * Describes that the annotated method or constructor is only
         * designed to handle lowercase characters.
         *
         * @since 0.0.1
         */
        LOWERCASE_ONLY,
        /**
         * Describes that the annotated method or constructor is only
         * designed to handle uppercase characters.
         *
         * @since 0.0.1
         */
        UPPERCASE_ONLY,
        /**
         * Describes that the annotated method or constructor is
         * designed to both support lowercase and uppercase characters.
         *
         * @since 0.0.1
         */
        LOWERCASE_AND_UPPERCASE
    }
}
