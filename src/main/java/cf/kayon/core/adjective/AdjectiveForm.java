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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a adjective form.
 * <p>
 * Immutable.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class AdjectiveForm
{

    /**
     * The cache pool.
     *
     * @since 0.2.0
     */
    private static final EnumMap<ComparisonDegree, EnumMap<Count, EnumMap<Gender, EnumMap<Case, AdjectiveForm>>>> pool = new EnumMap<>(ComparisonDegree.class);

    /**
     * All instances of this class.
     *
     * @since 0.2.0
     */
    private static final List<AdjectiveForm> allValues;

    static
    {
        List<AdjectiveForm> temporaryList = new ArrayList<>(108);
        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
        {
            EnumMap<Count, EnumMap<Gender, EnumMap<Case, AdjectiveForm>>> sub1 = new EnumMap<>(Count.class);
            pool.put(comparisonDegree, sub1);
            for (Count count : Count.values())
            {
                EnumMap<Gender, EnumMap<Case, AdjectiveForm>> sub2 = new EnumMap<>(Gender.class);
                sub1.put(count, sub2);
                for (Gender gender : Gender.values())
                {
                    EnumMap<Case, AdjectiveForm> sub3 = new EnumMap<>(Case.class);
                    sub2.put(gender, sub3);
                    for (Case caze : Case.values())
                    {
                        AdjectiveForm adjectiveForm = new AdjectiveForm(comparisonDegree, caze, count, gender);
                        sub3.put(caze, adjectiveForm);
                        temporaryList.add(adjectiveForm);
                    }
                }
            }
        }
        allValues = Collections.unmodifiableList(temporaryList);
    }

    /**
     * Returns a pooled instance of AdjectiveForm representing the specified arguments.
     *
     * @param comparisonDegree The comparison degree.
     * @param count            The count.
     * @param gender           The gender.
     * @param caze             The case.
     * @return A AdjectiveForm instance.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.2.0
     */
    @NotNull
    public static AdjectiveForm of(@NotNull ComparisonDegree comparisonDegree, @NotNull Count count, @NotNull Gender gender, @NotNull Case caze)
    {
        checkNotNull(comparisonDegree);
        checkNotNull(count);
        checkNotNull(gender);
        checkNotNull(caze);
        return pool.get(comparisonDegree).get(count).get(gender).get(caze);
    }

    /**
     * Gets all instances of this class in a list.
     * <p>
     * The entries in this list are ordered as follows:
     * <p>
     * All comparison degrees -&gt; All counts -&gt; All genders -&gt; All cases
     * <p>
     * For example:
     * <ol>
     * <li>Positive Singular Nominative Masculine</li>
     * <li>Positive Singular Nominative Feminine</li>
     * <li>Positive Singular Nominative Neuter</li>
     * <li>Positive Singular Genitive Masculine</li>
     * <li>...</li>
     * </ol>
     *
     * @return The list. The list is {@link Collections#unmodifiableList(List) unmodifiable}.
     * @since 0.2.0
     */
    @NotNull
    @Contract(pure = true)
    public static List<AdjectiveForm> values()
    {
        return allValues;
    }

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
     * The prefix of the property name as used by the {@link java.beans.PropertyChangeSupport} of {@link Adjective}.
     *
     * @since 0.2.0
     */
    @NotNull
    private final String propertyName;

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
    private AdjectiveForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        checkNotNull(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        this.comparisonDegree = comparisonDegree;
        this.caze = caze;
        this.count = count;
        this.gender = gender;
        this.propertyName = comparisonDegree + "_" + caze + "_" + count + "_" + gender + "_";
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
     * Returns the property name for usage with a {@link java.beans.PropertyChangeSupport}.
     *
     * @param suffix The suffix to append.
     * @return A property name.
     * @since 0.2.0
     */
    public String getPropertyName(String suffix)
    {
        return propertyName + suffix;
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
