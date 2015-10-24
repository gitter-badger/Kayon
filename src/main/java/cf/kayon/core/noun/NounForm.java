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

package cf.kayon.core.noun;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a noun form.
 * <p>
 * This class is only to be used when a form should be returned. When accepting a form, take in all the parameters separately.
 * <p>
 * Immutable.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class NounForm
{

    /**
     * The case.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Case caze;

    /**
     * The count.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Count count;

    /**
     * Constructs a new NounForm.
     *
     * @param caze  The case.
     * @param count The count.
     * @throws NullPointerException If any of the arguments is {@code null}.
     */
    public NounForm(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        this.caze = caze;
        this.count = count;
    }

    /**
     * @since 0.0.1
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NounForm nounForm = (NounForm) o;
        return Objects.equal(caze, nounForm.caze) &&
               Objects.equal(count, nounForm.count);
    }

    /**
     * @since 0.0.1
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("case", caze)
                          .add("count", count)
                          .toString();
    }

    /**
     * @since 0.0.1
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(caze, count);
    }

    /**
     * Gets the case.
     *
     * @return The case.
     * @since 0.0.1
     */
    @NotNull
    public Case getCaze()
    {

        return caze;
    }

    /**
     * Gets the count.
     *
     * @return The count.
     * @since 0.0.1
     */
    @NotNull
    public Count getCount()
    {
        return count;
    }
}
