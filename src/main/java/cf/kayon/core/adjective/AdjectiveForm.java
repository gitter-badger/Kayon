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

package cf.kayon.core.adjective;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a adjective form.
 * <p>
 * This class is only to be used when a form should be returned. When accepting a form, take in all the parameters separately.
 * <p>
 * Immutable.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class AdjectiveForm
{

    /**
     * The comparison degree.
     *
     * @since 0.0.1
     */
    @NotNull
    private final ComparisonDegree comparisonDegree;

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
     * The gender.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Gender gender;

    /**
     * Constructs a new AdjectiveForm.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    public AdjectiveForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        checkNotNull(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        this.comparisonDegree = comparisonDegree;
        this.caze = caze;
        this.count = count;
        this.gender = gender;
    }

    /**
     * @return The comparison degree.
     * @since 0.0.1
     */
    @NotNull
    public ComparisonDegree getComparisonDegree()
    {
        return comparisonDegree;
    }

    /**
     * @return The case.
     * @since 0.0.1
     */
    @NotNull
    public Case getCase()
    {
        return caze;
    }

    /**
     * @return The count.
     * @since 0.0.1
     */
    @NotNull
    public Count getCount()
    {
        return count;
    }

    /**
     * @return The gender.
     * @since 0.0.1
     */
    @NotNull
    public Gender getGender()
    {
        return gender;
    }

    /**
     * @since 0.0.1
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("comparisonDegree", comparisonDegree)
                          .add("case", caze)
                          .add("count", count)
                          .add("gender", gender)
                          .toString();
    }

    /**
     * @since 0.0.1
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdjectiveForm that = (AdjectiveForm) o;
        return Objects.equal(comparisonDegree, that.comparisonDegree) &&
               Objects.equal(caze, that.caze) &&
               Objects.equal(count, that.count) &&
               Objects.equal(gender, that.gender);
    }

    /**
     * @since 0.0.1
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(comparisonDegree, caze, count, gender);
    }
}
