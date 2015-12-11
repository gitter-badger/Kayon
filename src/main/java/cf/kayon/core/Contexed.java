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

/**
 * Describes an object that is bound to a {@link KayonContext}.
 *
 * @author Ruben Anders
 * @since 0.2.0
 */
public class Contexed
{
    /**
     * Holds the KayonContext of this Contexed object.
     *
     * @since 0.2.0
     */
    @NotNull
    private final KayonContext context;

    /**
     * The {@code protected} constructor of Contexed, so it can be instantiated by superclasses, but it cannot be instantiated alone.
     *
     * @param context The KayonContext of this Contexed object.
     * @since 0.2.0
     */
    protected Contexed(@NotNull KayonContext context)
    {
        checkNotNull(context);
        this.context = context;
    }

    /**
     * @since 0.2.0
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("context", context)
                          .toString();
    }

    /**
     * @since 0.2.0
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Contexed)) return false;
        Contexed contexed = (Contexed) o;
        return Objects.equal(context, contexed.context);
    }

    /**
     * @since 0.2.0
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(context);
    }

    /**
     * Gets the KayonContext of this Contexed object.
     * <p>
     * The returned object is guaranteed to be consistently the same instance.
     *
     * @return The KayonContext of this Contexed.
     * @since 0.2.0
     */
    @NotNull
    public KayonContext getContext()
    {
        return context;
    }
}
