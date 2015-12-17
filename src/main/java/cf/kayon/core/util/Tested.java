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

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Documents that an element is tested by unit or integration tests.
 * <p>
 * The {@link #value()} usually references to a fully qualified method reference.
 * <p>
 * This annotation can be repeated if it is tested by multiple unit/integration tests.
 *
 * Because this annotation serves no runtime purpose, its retention policy specifies that is only has to be
 * retained to class level, not until runtime. The level could also be
 *
 * @author Ruben Anders
 * @since 0.2.3
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Repeatable(MultiTested.class)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Tested
{
    /**
     * References what is testing this element.
     * <p>
     * For example, this could be a fully qualified reference to a test method (e.g. {@code com.example.impl.SomeImplTest.testSomeFunction})
     *
     * In IntelliJ IDEA, this reference can be easily copy-pasted into the string by right-clicking whilst your caret is on the test method and
     * choosing "Copy Reference".
     * Then paste whilst your caret is in the new empty string of the annotation on the element.
     *
     * @return A string documenting what is testing this element.
     * @since 0.2.3
     */
    @NotNull String value() default "";
}
