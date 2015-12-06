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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.Nullable;

@Immutable
public class Holder<T>
{

    @Nullable
    private final T value;

    public Holder(@Nullable T value)
    {
        this.value = value;
    }

    @Nullable
    public T getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("value", value)
                          .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Holder)) return false;
        Holder<?> holder = (Holder<?>) o;
        return Objects.equal(value, holder.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(value);
    }

}
