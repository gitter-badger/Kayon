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

import cf.kayon.core.*;
import cf.kayon.core.Count;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.*;
import java.util.*;

import static cf.kayon.core.util.StringUtil.requireNonEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a latin adjective.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class Adjective implements Vocab
{

    //region Fields
    /**
     * The declined forms of this adjective.
     *
     * @since 0.0.1
     */
    @NotNull
    private final ArrayTable<ComparisonDegree, Case, ArrayTable<Count, Gender, String>> declinedForms =
            ArrayTable.create(() -> new ObjectArrayIterator<>(ComparisonDegree.values(), 0), () -> new ObjectArrayIterator<>(Case.values(), 0));

    /**
     * The defined forms of this adjective.
     *
     * @since 0.0.1
     */
    @NotNull
    private final HashBasedTable<ComparisonDegree, Case, Table<Count, Gender, String>> definedForms = HashBasedTable.create(3, 6);

    /**
     * The root word of this adjective.
     *
     * @since 0.0.1
     */
    @NotNull
    private String rootWord;

    /**
     * The declension of this adjective.
     *
     * @since 0.0.1
     */
    @Nullable
    private AdjectiveDeclension adjectiveDeclension;

    /**
     * The translations of this adjective.
     *
     * @since 0.0.1
     */
    @NotNull
    private final HashMap<String, String> translations = Maps.newHashMap();

    /**
     * Whether this adjective allows positive forms.
     *
     * @since 0.0.1
     */
    private boolean allowsPositive = true;

    /**
     * Whether this adjective allows comparative forms.
     *
     * @since 0.0.1
     */
    private boolean allowsComparative = true;

    /**
     * Whether this adjective allows superlative forms.
     *
     * @since 0.0.1
     */
    private boolean allowsSuperlative = true;

    /**
     * The property change support for this class.
     * <p>
     * List of property change events fired by this class:
     * <p>
     * <table summary="">
     * <thead>
     * <tr>
     * <td>Property name</td>
     * <td>Fired by</td>
     * <td>Vetoable?</td>
     * <td>Triggers</td>
     * <td>Default checks</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>{@code $COMPARISONDEGREE_allowed}</td>
     * <td>{@link #setAllows(ComparisonDegree, boolean)}, {@link #setAllowsPositive(boolean)},
     * {@link #setAllowsComparative(boolean)}, {@link #setAllowsSuperlative(boolean)}</td>
     * <td>Yes</td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code $COMPARISONDEGREE_$CASE_$COUNT_$GENDER_defined}</td>
     * <td>{@link #defineForm(ComparisonDegree, Case, Count, Gender, String)}</td>
     * <td>Yes</td>
     * <td>{@link #_declineIntoBuffer()}</td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code $COMPARISONDEGREE_$CASE_$COUNT_$GENDER_declined}</td>
     * <td>{@link #_declineIntoBuffer()}</td>
     * <td>No</td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code adjectiveDeclension}</td>
     * <td>{@link #setAdjectiveDeclension(AdjectiveDeclension)}</td>
     * <td>Yes</td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code rootWord}</td>
     * <td>{@link #setRootWord(String)}</td>
     * <td>Yes</td>
     * <td></td>
     * <td>Not {@code null}, not {@link String#isEmpty() empty}.</td>
     * </tr>
     * <tr>
     * <td>{@code uuid}</td>
     * <td>{@link #initializeUuid(UUID)}</td>
     * <td>No (as specified by {@link Vocab#initializeUuid(UUID)})</td>
     * <td></td>
     * <td></td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @since 0.0.1
     */
    @NotNull
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * The vetoable change support for this class.
     * <p>
     * List of vetoable change events fired by this class can be reviewed by taking a look at {@link #changeSupport}.
     *
     * @since 0.0.1
     */
    @NotNull
    private final VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);
    //endregion

    //region Constructors

    /**
     * Constructs a new Adjective.
     * <p>
     * The {@code rootWord} argument should be lowercase only (see annotation).
     *
     * @param adjectiveDeclension An AdjectiveDeclension or {@code null}.
     * @param rootWord            The root word of the adjective.
     * @throws NullPointerException     If {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Adjective(@Nullable AdjectiveDeclension adjectiveDeclension, @NotNull String rootWord)
    {
        requireNonEmpty(rootWord);
        this.adjectiveDeclension = adjectiveDeclension;
        this.rootWord = rootWord;
        _declineIntoBuffer(); // So one does not have to deal with vetoexceptions
    }

    /**
     * Constructs a new Adjective with no AdjectiveDeclension.
     * <p>
     * The {@code rootWord} argument should be lowercase only (see annotation).
     *
     * @param rootWord The root word of the adjective.
     * @throws NullPointerException     If {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Adjective(@NotNull String rootWord)
    {
        this(null, rootWord);
    }
    //endregion

    //region Getters and Setters

    /**
     * Gets the root word of this adjective.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @return The root word. Never {@code null}, never {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    public String getRootWord()
    {
        return rootWord;
    }

    /**
     * Sets the root word of this adjective.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param rootWord The root word to set.
     * @throws NullPointerException     If {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @throws PropertyVetoException    If the {@link VetoableChangeSupport} of this class decides that the new value is not valid.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void setRootWord(@NotNull String rootWord) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("rootWord", this.rootWord, rootWord);
        String oldValue = this.rootWord;
        this.rootWord = rootWord;
        changeSupport.firePropertyChange("rootWord", oldValue, rootWord);
    }

    /**
     * Gets the declension of this adjective.
     *
     * @return The declension of this adjective. {@code null} if this adjective has no declension.
     * @since 0.0.1
     */
    @Nullable
    public AdjectiveDeclension getAdjectiveDeclension()
    {
        return adjectiveDeclension;
    }

    /**
     * Sets the declension of this adjective.
     *
     * @param adjectiveDeclension The new declension. May be null to set this adjective into no-declension-mode.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value is not valid.
     * @since 0.0.1
     */
    public void setAdjectiveDeclension(@Nullable AdjectiveDeclension adjectiveDeclension) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("adjectiveDeclension", this.adjectiveDeclension, adjectiveDeclension);
        AdjectiveDeclension oldValue = this.adjectiveDeclension;
        this.adjectiveDeclension = adjectiveDeclension;
        changeSupport.firePropertyChange("adjectiveDeclension", oldValue, adjectiveDeclension);
    }

    /**
     * Returns whether this adjective allows positive forms.
     *
     * @return Whether this adjective allows positive forms.
     * @see #allows(ComparisonDegree)
     * @since 0.0.1
     */
    public boolean allowsPositive()
    {
        return allowsPositive;
    }

    /**
     * Sets whether this adjective allows positive forms.
     *
     * @param allowsPositive Whether this adjective allows positive forms.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value is not valid.
     * @see #setAllows(ComparisonDegree, boolean)
     * @since 0.0.1
     */
    public void setAllowsPositive(boolean allowsPositive) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("POSITIVE_allowed", this.allowsPositive, allowsPositive);
        boolean oldValue = this.allowsPositive;
        this.allowsPositive = allowsPositive;
        changeSupport.firePropertyChange("POSTIVE_allowed", oldValue, allowsPositive);
    }

    /**
     * Returns whether this adjective allows comparative forms.
     *
     * @return Whether this adjective allows comparative forms.
     * @see #allows(ComparisonDegree)
     * @since 0.0.1
     */
    public boolean allowsComparative()
    {
        return allowsComparative;
    }

    /**
     * Sets whether this adjective allows comparative forms.
     *
     * @param allowsComparative Whether this adjective allows comparative forms.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value is not valid.
     * @see #setAllows(ComparisonDegree, boolean)
     * @since 0.0.1
     */
    public void setAllowsComparative(boolean allowsComparative) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("COMPARATIVE_allowed", this.allowsComparative, allowsComparative);
        boolean oldValue = this.allowsComparative;
        this.allowsComparative = allowsComparative;
        changeSupport.firePropertyChange("COMPARATIVE_allowed", oldValue, allowsComparative);
    }

    /**
     * Returns whether this adjective allows superlative forms.
     *
     * @return Whether this adjective allows superlative forms.
     * @see #allows(ComparisonDegree)
     * @since 0.0.1
     */
    public boolean allowsSuperlative()
    {
        return allowsSuperlative;
    }

    /**
     * Sets whether this adjective allows positive forms.
     *
     * @param allowsSuperlative Whether this adjective allows superlative forms.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value is not valid.
     * @see #setAllows(ComparisonDegree, boolean)
     * @since 0.0.1
     */
    public void setAllowsSuperlative(boolean allowsSuperlative) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("SUPERLATIVE_allowed", this.allowsSuperlative, allowsSuperlative);
        boolean oldValue = this.allowsSuperlative;
        this.allowsSuperlative = allowsSuperlative;
        changeSupport.firePropertyChange("SUPERLATIVE_allowed", oldValue, allowsSuperlative);
    }

    /**
     * Returns whether this adjective allows the specified comparison degree.
     *
     * @param comparisonDegree The comparison degree to check for.
     * @return Whether this adjective allows the specified comparison degree.
     * @throws NullPointerException If the specified {@code comparisonDegree} is {@code null}.
     * @see #allowsPositive()
     * @see #allowsComparative()
     * @see #allowsSuperlative()
     * @since 0.0.1
     */
    public boolean allows(@NotNull ComparisonDegree comparisonDegree)
    {
        checkNotNull(comparisonDegree);
        switch (comparisonDegree)
        {
            case POSITIVE:
                return this.allowsPositive();
            case COMPARATIVE:
                return this.allowsComparative();
            case SUPERLATIVE:
                return this.allowsSuperlative();
            default:
                return false;
        }
    }

    /**
     * Sets whether this adjective allows the specified comparison degree.
     *
     * @param comparisonDegree The comparison degree to check for.
     * @param allows           Whether to allow or disallow.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value is not valid.
     * @see #setAllowsPositive(boolean)
     * @see #setAllowsComparative(boolean)
     * @see #setAllowsSuperlative(boolean)
     * @since 0.0.1
     */
    public void setAllows(@NotNull ComparisonDegree comparisonDegree, boolean allows) throws PropertyVetoException
    {
        switch (comparisonDegree)
        {
            case POSITIVE:
                this.setAllowsPositive(allows);
                break;
            case COMPARATIVE:
                this.setAllowsComparative(allows);
                break;
            case SUPERLATIVE:
                this.setAllowsSuperlative(allows);
                break;
        }
    }
    //endregion

    //region Defining Forms

    /**
     * Defines a form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @param form             The form to define. A empty string or a null value results in the defined form being removed.
     * @throws NullPointerException     If any of the arguments (except for {@code form}) is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @throws PropertyVetoException    If the {@link VetoableChangeSupport} of this class decides that new value is not valid.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void defineForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @Nullable String form)
            throws PropertyVetoException
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        if (form == null || form.isEmpty())
        {
            // Fires its own events and throws PropertyVetoExceptions as well
            removeDefinedForm(comparisonDegree, caze, count, gender);
            return;
        }

        String oldForm = getDefinedForm(comparisonDegree, caze, count, gender);
        String propertyName = comparisonDegree + "_" + caze + "_" + count + "_" + gender + "_defined";
        vetoSupport.fireVetoableChange(propertyName, oldForm, form);

        Table<Count, Gender, String> subTable = this.definedForms.get(comparisonDegree, caze);
        if (subTable == null)
        {
            subTable = HashBasedTable.create(2, 3);
            this.definedForms.put(comparisonDegree, caze, subTable);
        }
        subTable.put(count, gender, form);
        changeSupport.firePropertyChange(propertyName, oldForm, form);
    }

    /**
     * Returns whether a form has been defined.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @return Whether the form has been defined.
     * @since 0.0.1
     */
    public boolean isFormDefined(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        Table<Count, Gender, String> subTable = definedForms.get(comparisonDegree, caze);
        return subTable != null && subTable.contains(count, gender);
    }

    /**
     * Gets a defined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param comparisonDegree The comparsion degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @return The defined form. {@code null} if no such form has been defined.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDefinedForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        @Nullable
        Table<Count, Gender, String> subTable = definedForms.get(comparisonDegree, caze);
        if (subTable == null)
            return null;

        // Nullable
        return subTable.get(count, gender);
    }

    /**
     * Gets an immutable view of the defined forms.
     * <p>
     * This method trusts the caller, since the subtables are not immutable views. Be careful, do not modify them.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @return A table of forms. The table itself is backed in the adjective, if there are changes to the defined forms, they will be reflected in the table.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    public Table<ComparisonDegree, Case, Table<Count, Gender, String>> getDefinedForms()
    {
        return Tables.unmodifiableTable(this.definedForms);
    }

    /**
     * Removes a defined form.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value {@code null} is not valid.
     * @since 0.0.1
     */
    public void removeDefinedForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
            throws PropertyVetoException
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        String propertyName = comparisonDegree + "_" + caze + "_" + count + "_" + gender + "_defined";
        String oldForm = getDefinedForm(comparisonDegree, caze, count, gender);
        vetoSupport.fireVetoableChange(propertyName, oldForm, null);

        @Nullable
        Table<Count, Gender, String> subTable = this.definedForms.get(comparisonDegree, caze);
        if (subTable == null)
            return; // No changes performed (removing a non-existent form)
        subTable.remove(count, gender);
        changeSupport.firePropertyChange(propertyName, oldForm, null);
    }
    //endregion

    //region Declension-based operations

    /**
     * Gets a declined form. This always returns what the underlying declension determined or what has been declined out of defined forms and equal forms.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @return A declined form.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDeclinedForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        if (this.adjectiveDeclension == null)
            return null;
        @Nullable
        ArrayTable<Count, Gender, String> subTable = this.declinedForms.get(comparisonDegree, caze);
        if (subTable == null)
            return null;
        return subTable.get(count, gender);
    }

    /**
     * Saves a declined form if it does not exist yet.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @param form             The form.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If the {@code form} is {@link String#isEmpty() empty} or
     *                                  the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void putDeclinedFormIfAbsent(
            @NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String form)
    {
        if (getDeclinedForm(comparisonDegree, caze, count, gender) == null)
            putDeclinedForm(comparisonDegree, caze, count, gender, form);
    }

    /**
     * Helper method for saving a declined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @param form             The form to save. If it is {@code null} or an {@link String#isEmpty() empty} string, {@code null} is saved.
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void putDeclinedForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @Nullable String form)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        if (form != null && form.isEmpty())
            form = null;

        @Nullable
        ArrayTable<Count, Gender, String> subTable = this.declinedForms.get(comparisonDegree, caze);
        if (subTable == null) // First time only or after declension had been cleared
        {
            subTable = ArrayTable.create(() -> new ObjectArrayIterator<>(Count.values()), () -> new ObjectArrayIterator<>(Gender.values()));
            this.declinedForms.put(comparisonDegree, caze, subTable);
        }
        String oldForm = subTable.get(count, gender);
        String propertyName = comparisonDegree + "_" + caze + "_" + count + "_" + gender + "_declined";
        subTable.put(count, gender, form); // inserting null is equal to .erase()
        changeSupport.firePropertyChange(propertyName, oldForm, form);
    }

    /**
     * Declines all forms into the buffer. Called by the property change listener.
     * <p>
     * All declining logic is lowercase-supporting only.
     *
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void _declineIntoBuffer()
    {
        if (this.adjectiveDeclension == null)
        { // Properly invoke all change/veto listeners
            for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
                for (Case caze : Case.values())
                    for (Count count : Count.values())
                        for (Gender gender : Gender.values())
                        {
                            putDeclinedForm(comparisonDegree, caze, count, gender, null); // Fire PropertyChangeEvent, override old value
                        }
        }

        // Equal forms
        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
            for (Case caze : Case.values())
                for (Count count : Count.values())
                    for (Gender gender : Gender.values())
                        if (this.isFormDefined(comparisonDegree, caze, count, gender))
                        {
                            // noinspection ConstantConditions
                            Set<AdjectiveForm> equalForms = adjectiveDeclension.getEqualForms(comparisonDegree, caze, count, gender);
                            if (equalForms != null)
                                equalForms.stream().filter(form -> !form.equals(new AdjectiveForm(comparisonDegree, caze, count, gender))).forEach(form -> {
                                    String definedFormOrNull = getDefinedForm(comparisonDegree, caze, count, gender);
                                    if (definedFormOrNull != null)
                                    {
                                        putDeclinedFormIfAbsent(form.getComparisonDegree(), form.getCase(), form.getCount(), form.getGender(),
                                                                definedFormOrNull);
                                    }
                                });
                        }

        // Declining
        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
            for (Case caze : Case.values())
                for (Count count : Count.values())
                    for (Gender gender : Gender.values())
                        try
                        {
                            putDeclinedFormIfAbsent(comparisonDegree, caze, count, gender,
                                                    this.adjectiveDeclension.decline(comparisonDegree, caze, count, gender, getRootWord()));
                        } catch (FormingException ignored) {} // null will reside
    }
    //endregion

    /**
     * Precondition to call to make sure a passed comparison degree is {@link #allows(ComparisonDegree) allowed}.
     *
     * @param comparisonDegree The comparison degree to check.
     * @return The comparison degree itself, if it is allowed.
     * @throws IllegalArgumentException If the comparison degree is disallowed.
     * @throws NullPointerException     If the {@code comparisonDegree} is {@code null}.
     * @since 0.0.1
     */
    @NotNull
    private ComparisonDegree requireAllowedComparisonDegree(@NotNull ComparisonDegree comparisonDegree)
    {
        if (!allows(comparisonDegree)) // Delegates null check
            throw new IllegalArgumentException("Unallowed Comparison degree " + comparisonDegree.toString());
        return comparisonDegree;
    }

    /**
     * Gets a form - if a defined form exists, gets the defined form, if it does not exist, return a declined form.
     * {@code null} if there is both no defined and declined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @return The form. {@code null} if there is both no defined and declined form.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        @Nullable
        String defined = this.getDefinedForm(comparisonDegree, caze, count, gender); // Delegates not null checks
        if (defined != null)
            return defined;
        return this.getDeclinedForm(comparisonDegree, caze, count, gender);
    }

    /**
     * @since 0.0.1
     * @deprecated Use the JavaFX graphical interface instead. Scheduled for removal as of 0.1.0.
     */
    @NotNull
    @Override
    @Deprecated
    public List<String> commandLineRepresentation()
    {
        List<String> buffer = new ArrayList<>(53);

        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
        {
            if (this.allows(comparisonDegree))
            {
                buffer.add("+----------+---------------+---------------+---------------+");
                buffer.add("| " + comparisonDegree.toString().substring(0, 8) + " |   MASCULINE   |    FEMININE   |     NEUTER    |");
                buffer.add("+----------+---------------+---------------+---------------+");
                for (Count count : Count.values())
                {
                    for (Case caze : Case.values())
                    {
                        StringBuilder builder = new StringBuilder(60);

                        builder.append("|");
                        builder.append(Strings.padStart(caze.toString(), 10, ' '));
                        builder.append("|");

                        for (Gender gender : Gender.values())
                        {
                            @Nullable
                            String formOrNull = getForm(comparisonDegree, caze, count, gender);
                            formOrNull = formOrNull == null ? "???????????????" : formOrNull;
                            builder.append(isFormDefined(comparisonDegree, caze, count, gender) ? "$" : " ");
                            builder.append(Strings.padEnd(formOrNull, 14, ' '));
                            builder.append("|");
                        }

                        buffer.add(builder.toString());
                    }
                    buffer.add("+----------+---------------+---------------+---------------+");
                }

            } else
            {
                buffer.add(comparisonDegree.toString() + " is disallowed");
            }
            buffer.add("");
        }

        return buffer;
    }

    /**
     * The UUID of this Adjective.
     *
     * @since 0.0.1
     */
    private UUID uuid;

    /**
     * @since 0.0.1
     */
    @Nullable
    @Override
    public UUID getUuid()
    {
        return uuid;
    }

    /**
     * @since 0.0.1
     */
    @Override
    public void initializeUuid(@NotNull UUID uuid)
    {
        checkNotNull(uuid);
        if (this.uuid != null)
            throw new IllegalStateException("UUID has already been initialized");
        this.uuid = uuid;
        changeSupport.firePropertyChange("uuid", null, uuid);
    }

    /**
     * @since 0.0.1
     */
    @NotNull
    @Override
    public Map<String, String> getTranslations()
    {
        return translations;
    }

    // since 0.0.1
    {
        addPropertyChangeListener(evt -> {
            String propertyName = evt.getPropertyName();
            if (propertyName.endsWith("_defined") || propertyName.equals("adjectiveDeclension") || propertyName.equals("rootWord") || propertyName.endsWith("_allowed"))
                _declineIntoBuffer();
        });

        addVetoableChangeListener(evt -> {
            String propertyName = evt.getPropertyName();
            Object newValue = evt.getNewValue();
            if (newValue == null && !propertyName.endsWith("_defined") || !propertyName.equals("adjectiveDeclension"))
                throw new PropertyVetoException("New value may not be null!", evt);
            if (newValue instanceof String && ((String) newValue).isEmpty() && propertyName.equals("rootWord"))
                throw new PropertyVetoException("New String value may not be empty!", evt);
        });
    }

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     * @since 0.0.1
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     * @since 0.0.1
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @see VetoableChangeSupport#addVetoableChangeListener(VetoableChangeListener)
     * @since 0.0.1
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
        vetoSupport.addVetoableChangeListener(listener);
    }

    /**
     * @see VetoableChangeSupport#removeVetoableChangeListener(VetoableChangeListener)
     * @since 0.0.1
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
        vetoSupport.removeVetoableChangeListener(listener);
    }
}
