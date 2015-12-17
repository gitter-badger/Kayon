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
import cf.kayon.core.util.NotTested;
import cf.kayon.core.util.Tested;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
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
     * The immutable cache pool.
     *
     * @since 0.2.0
     */
    @NotNull
    private static final Map<Count, Map<Case, NounForm>> pool;

    /**
     * An immutable list of all values.
     *
     * @since 0.2.0
     */
    @NotNull
    private static final List<NounForm> allValues;

    // since 0.0.1
    static
    {
        ImmutableList.Builder<NounForm> builder = ImmutableList.<NounForm>builder();
        EnumMap<Count, EnumMap<Case, NounForm>> poolBuilder = new EnumMap<>(Count.class);
        for (Count count : Count.values())
        {
            EnumMap<Case, NounForm> currentSub = new EnumMap<>(Case.class);
            poolBuilder.put(count, currentSub);
            for (Case caze : Case.values())
            {
                NounForm currentForm = new NounForm(caze, count);
                currentSub.put(caze, currentForm);
                builder.add(currentForm);
            }
        }
        pool = Maps.immutableEnumMap(poolBuilder);
        allValues = builder.build();
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
    @SuppressWarnings("FieldNotUsedInToString")
    @NotNull
    private final String propertyName;

    /**
     * The string returned by {@link #toString()}.
     *
     * @since 0.2.3
     */
    @NotNull
    private final String toStringRepresentation;

    /**
     * Constructs a new NounForm.
     * <p>
     * This constructor is <strong>not</strong> to be invoked from outside this class.
     * Use {@link #of(Case, Count)} instead.
     *
     * @param caze  The case.
     * @param count The count.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @Tested("cf.kayon.core.noun.NounFormTest.testEquals")
    private NounForm(@NotNull final Case caze, @NotNull final Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        this.caze = caze;
        this.count = count;
        this.propertyName = caze + "_" + count + "_";
        this.toStringRepresentation = StringUtils.capitalize(caze.name().toLowerCase().substring(0, 3)) +
                                      StringUtils.capitalize(count.name().toLowerCase().substring(0, 2));
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
    @Tested("cf.kayon.core.noun.NounFormTest.testOf")
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
     * @return A List of values. The list is {@link ImmutableList immutable}.
     * @since 0.2.0
     */
    @Contract(pure = true)
    @NotNull
    @Tested("cf.kayon.core.noun.NounFormTest.testValues")
    public static List<NounForm> values()
    {
        return allValues;
    }

    /**
     * @since 0.0.1
     */
    @Tested("cf.kayon.core.noun.NounFormTest.testToString")
    @Override
    public String toString()
    {
        return toStringRepresentation;
    }

    /**
     * {@inheritDoc}
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
    @Tested("cf.kayon.core.noun.NounFormTest.testEquals")
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
    @NotTested
    @Override
    public int hashCode()
    {
        return Objects.hashCode(caze, count); // Property name does not need to be hashed
    }

    /**
     * Gets the case.
     *
     * @return The case.
     * @since 0.0.1
     */
    @Tested("cf.kayon.core.noun.NounFormTest.testGetCase")
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
    @Tested("cf.kayon.core.noun.NounFormTest.testGetCount")
    @NotNull
    public Count getCount()
    {
        return count;
    }

    /**
     * Returns the property name for usage with a {@link java.beans.PropertyChangeSupport}.
     * <p>
     * The returned string is in the format {@code $Case_$Count_$Suffix}.
     *
     * @param suffix The suffix to append.
     * @return A property name.
     * @throws NullPointerException     If {@code suffix} is {@code null}.
     * @throws IllegalArgumentException If {@code suffix} is {@link String#isEmpty() empty}.
     * @since 0.2.0
     */
    @Tested("cf.kayon.core.noun.NounFormTest.testGetPropertyName")
    public String getPropertyName(@NotNull @NonNls String suffix)
    {
        checkNotEmpty(suffix);
        return propertyName + suffix;
    }
}
