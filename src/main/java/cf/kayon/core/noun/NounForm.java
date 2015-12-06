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
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a noun form.
 * <p>
 * The state of objects of this class is immutable and is guaranteed to be never modified
 * by the {@code final} keyword.
 * <p>
 * The possible instances of this class are pooled. To get an instance from the pool, use the static factory method {@link #of(Case, Count)}.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
@Immutable
public class NounForm
{

    /**
     * The cache pool.
     *
     * @since 0.2.0
     */
    @NotNull
    private static final EnumMap<Count, EnumMap<Case, NounForm>> pool = new EnumMap<>(Count.class);
    /**
     * An unmodifiable list of all values.
     *
     * @since 0.2.0
     */
    @NotNull
    private static final List<NounForm> allValues;

    static
    {
        List<NounForm> temporaryList = new ArrayList<>(12);
        for (Count count : Count.values())
        {
            EnumMap<Case, NounForm> currentSub = new EnumMap<>(Case.class);
            pool.put(count, currentSub);
            for (Case caze : Case.values())
            {
                NounForm currentForm = new NounForm(caze, count);
                currentSub.put(caze, currentForm);
                temporaryList.add(currentForm);
            }
        }
        allValues = Collections.unmodifiableList(temporaryList);
    }

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
     * The prefix of the property name as used by the {@link java.beans.PropertyChangeSupport} of {@link Noun}.
     *
     * @since 0.2.0
     */
    @NotNull
    private final String propertyName;

    /**
     * Constructs a new NounForm.
     *
     * @param caze  The case.
     * @param count The count.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    private NounForm(@NotNull final Case caze, @NotNull final Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        this.caze = caze;
        this.count = count;
        this.propertyName = caze + "_" + count + "_";
    }

    /**
     * Returns a pooled instance of NounForm representing the specified case and count.
     *
     * @param caze  The case of the NounForm to be returned.
     * @param count The count of the NounForm to be returned.
     * @return A NounForm instance.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.2.0
     */
    @NotNull
    public static NounForm of(@NotNull final Case caze, @NotNull final Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return pool.get(count).get(caze);
    }

    /**
     * Gets all possible values of this class.
     * <p>
     * The entries in this list are ordered as follows:
     * <p>
     * All counts -&gt; All cases
     * <p>
     * For example:
     * <ul>
     * <li>Singular Nominative</li>
     * <li>Singular Genitive</li>
     * <li>Singular Dative</li>
     * <li>Singular ...</li>
     * <li>Plural ...</li>
     * </ul>
     *
     * @return A List of values. The list is {@link Collections#unmodifiableList(List) unmodifiable}.
     * @since 0.2.0
     */
    @Contract(pure = true)
    @NotNull
    public static List<NounForm> values()
    {
        return allValues;
    }

    /**
     * @since 0.0.1
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("caze", caze)
                          .add("count", count)
                          .add("propertyName", propertyName)
                          .toString();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Implementation note:</strong> The constraints of this class define that any two NounForms being equal as returned by this function
     * must also be identity-equal. More formally, the following expression will never throw an {@link Exception}:
     * <pre>{@code
     * NounForm a = ...;
     * NounForm b = ...;
     * if (a.equals(b))
     *   if (a != b)
     *     throw new Exception();
     * }</pre>
     *
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
    public Case getCase()
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

    /**
     * Returns the property name for usage with a {@link java.beans.PropertyChangeSupport}.
     * <p>
     * The string is in the format {@code $ComparisonDegree_$Case_$Count_$Gender_$Suffix}.
     *
     * @param suffix The suffix to append.
     * @return A property name.
     * @since 0.2.0
     */
    public String getPropertyName(String suffix)
    {
        return propertyName + suffix;
    }
}
