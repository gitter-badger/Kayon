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

import java.lang.annotation.*;

/**
 * Documents that the annotated constructor or method does not need to be tested.
 * Usually, a target is annotated with this annotation
 * if it serves for a very simple purpose e.g. is auto-generated (e.g. hashCode()),
 * simply delegates to a library method, is a encapsulated private method
 * that is tested using its public API or is an enum/interface/annotation (these types
 * do not provide implementations).
 *
 * @author Ruben Anders
 * @since 0.2.3
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface NotTested
{
}
