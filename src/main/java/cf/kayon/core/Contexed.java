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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class Contexed
{
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("context", context)
                          .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Contexed)) return false;
        Contexed contexed = (Contexed) o;
        return Objects.equal(context, contexed.context);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(context);
    }

    /*
         * Thread safety notice
         *
         * All set fields are final, guaranteeing memory visibility.
         */
    @NotNull
    public KayonContext getContext()

    {
        return context;
    }

    @NotNull
    private final KayonContext context;

    /*
     * Thread safety notice
     *
     * All set fields are final, guaranteeing memory visibility.
     */
    protected Contexed(@NotNull KayonContext context)
    {
        checkNotNull(context);
        this.context = context;
    }
}
