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

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object which can be fully cloned.
 *
 * @param <T> The type of this object, e.g. when implementing {@code DeepCopyable} in a class {@code MySample}, you should write
 * {@code class MySample implements DeepCopyable<MySample>}.
 * @author Ruben Anders
 * @since 0.2.0
 */
@ThreadSafe
public interface DeepCopyable<T extends DeepCopyable<T>>
{

    /**
     * Creates a <strong>deep</strong> copy of the current object and returns it.
     * <p>
     * This means that the copy returned is not connected to this object in any state, but they may share immutable objects.
     *
     * @return A deep copy.
     * @since 0.2.0
     */
    @NotNull
    T copyDeep();
}
