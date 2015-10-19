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

import static com.google.common.base.Preconditions.checkNotNull;

public class NounForm
{

    private final Case caze;

    private final Count count;

    public NounForm(Case caze, Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        this.caze = caze;
        this.count = count;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NounForm nounForm = (NounForm) o;
        return Objects.equal(caze, nounForm.caze) &&
               Objects.equal(count, nounForm.count);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("case", caze)
                          .add("count", count)
                          .toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(caze, count);
    }

    public Case getCaze()
    {

        return caze;
    }

    public Count getCount()
    {
        return count;
    }
}
