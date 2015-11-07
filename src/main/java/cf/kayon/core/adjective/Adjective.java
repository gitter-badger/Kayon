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

import cf.kayon.core.CaseHandling;
import cf.kayon.core.FormingException;
import cf.kayon.core.StandardVocab;
import cf.kayon.core.Vocab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static cf.kayon.core.util.StringUtil.requireNonEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a latin adjective.
 * <p>
 * List of property change events fired by this class:
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
 * <td>{@link #defineForm(AdjectiveForm, String)}</td>
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
 * @author Ruben Anders
 * @since 0.0.1
 */
public class Adjective extends StandardVocab
{

    //region Fields
    /**
     * The declined forms of this adjective.
     *
     * @since 0.0.1
     */
    @NotNull
    private final HashMap<AdjectiveForm, String> declinedForms = new HashMap<>(106);

    /**
     * The defined forms of this adjective.
     *
     * @since 0.0.1
     */
    @NotNull
    private final HashMap<AdjectiveForm, String> definedForms = new HashMap<>(106);

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
        _declineIntoBuffer(); // So one does not have to deal with PropertyVetoExceptions
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
     * @param adjectiveForm The adjective form.
     * @param form          The form to define. A empty string or a null value results in the defined form being removed.
     * @throws NullPointerException     If any of the arguments (except for {@code form}) is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @throws PropertyVetoException    If the {@link VetoableChangeSupport} of this class decides that new value is not valid.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void defineForm(@NotNull AdjectiveForm adjectiveForm, @Nullable String form)
            throws PropertyVetoException
    {
        requireAllowedComparisonDegree(adjectiveForm);

        if (form == null || form.isEmpty())
        {
            // Fires its own events and throws PropertyVetoExceptions as well
            removeDefinedForm(adjectiveForm);
            return;
        }

        String oldForm = getDefinedForm(adjectiveForm);
        String propertyName =
                adjectiveForm.getComparisonDegree() + "_" + adjectiveForm.getCase() + "_" + adjectiveForm.getCount() + "_" + adjectiveForm.getGender() + "_defined";
        vetoSupport.fireVetoableChange(propertyName, oldForm, form);

        definedForms.put(adjectiveForm, form);
        changeSupport.firePropertyChange(propertyName, oldForm, form);
    }

    /**
     * Returns whether a form has been defined.
     *
     * @param adjectiveForm The adjective form.
     * @return Whether the form has been defined.
     * @since 0.0.1
     */
    public boolean isFormDefined(@NotNull AdjectiveForm adjectiveForm)
    {
        requireAllowedComparisonDegree(adjectiveForm);

        return definedForms.containsKey(adjectiveForm);
    }

    /**
     * Gets a defined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param adjectiveForm The adjective form.
     * @return The defined form. {@code null} if no such form has been defined.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDefinedForm(@NotNull AdjectiveForm adjectiveForm)
    {
        requireAllowedComparisonDegree(adjectiveForm);
        return definedForms.get(adjectiveForm);
    }

    /**
     * Removes a defined form.
     *
     * @param adjectiveForm The adjective form.
     * @throws PropertyVetoException If the {@link VetoableChangeSupport} of this class decides that the new value {@code null} is not valid.
     * @since 0.0.1
     */
    public void removeDefinedForm(@NotNull AdjectiveForm adjectiveForm)
            throws PropertyVetoException
    {
        requireAllowedComparisonDegree(adjectiveForm);

        String propertyName =
                adjectiveForm.getComparisonDegree() + "_" + adjectiveForm.getCase() + "_" + adjectiveForm.getCount() + "_" + adjectiveForm.getGender() + "_defined";
        String oldForm = definedForms.get(adjectiveForm);
        vetoSupport.fireVetoableChange(propertyName, oldForm, null);

        definedForms.remove(adjectiveForm);

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
     * @param adjectiveForm The adjective form.
     * @return A declined form.
     * @throws NullPointerException     If any of the arguments is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDeclinedForm(@NotNull AdjectiveForm adjectiveForm)
    {
        requireAllowedComparisonDegree(adjectiveForm);
        if (this.adjectiveDeclension == null)
            return null;
        return declinedForms.get(adjectiveForm);
    }

    /**
     * Helper method for saving a declined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param adjectiveForm The adjective form.
     * @param form          The form to save. If it is {@code null} or an {@link String#isEmpty() empty} string, {@code null} is saved.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void putDeclinedForm(@NotNull AdjectiveForm adjectiveForm, @Nullable String form)
    {
        requireAllowedComparisonDegree(adjectiveForm);

        String oldForm = declinedForms.get(adjectiveForm);
        String propertyName =
                adjectiveForm.getComparisonDegree() + "_" + adjectiveForm.getCase() + "_" + adjectiveForm.getCount() + "_" + adjectiveForm.getGender() + "_declined";
        if (form == null || form.isEmpty())
        {
            definedForms.remove(adjectiveForm);
            changeSupport.firePropertyChange(propertyName, oldForm, null);
        } else
        {
            definedForms.put(adjectiveForm, form);
            changeSupport.firePropertyChange(propertyName, oldForm, form);
        }
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
            for (AdjectiveForm adjectiveForm : AdjectiveForm.values())
            {
                putDeclinedForm(adjectiveForm, null); // Fire PropertyChangeEvent, override old value
            }
        }

        // Equal forms
        for (AdjectiveForm adjectiveForm : AdjectiveForm.values())
            if (this.isFormDefined(adjectiveForm))
            {
                Set<AdjectiveForm> equalForms = adjectiveDeclension.getEqualForms(adjectiveForm);
                if (equalForms != null)
                    equalForms.stream().filter(form -> !form.equals(adjectiveForm)).forEach(form -> {
                        String definedFormOrNull = getDefinedForm(adjectiveForm);
                        if (definedFormOrNull != null && !declinedForms.containsKey(adjectiveForm))
                        {
                            putDeclinedForm(adjectiveForm, definedFormOrNull);
                        }
                    });
            }

        // Declining
        for (AdjectiveForm adjectiveForm : AdjectiveForm.values())
            try
            {
                if (!declinedForms.containsKey(adjectiveForm))
                    putDeclinedForm(adjectiveForm, this.adjectiveDeclension.decline(adjectiveForm, rootWord));
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
            throw new IllegalArgumentException("Disallowed Comparison degree " + comparisonDegree.toString());
        return comparisonDegree;
    }

    /**
     * Precondition to call to make sure a passed AdjectiveForm is not {@code null} and its comparison degree is allowed.
     *
     * @param adjectiveForm The adjective form to check.
     * @return The adjective form itself, if it is valid.
     * @throws IllegalArgumentException If the comparison degree is disallowed.
     * @throws NullPointerException     If {@code adjectiveForm} is {@code null}.
     * @since 0.2.0
     */
    @NotNull
    private AdjectiveForm requireAllowedComparisonDegree(@NotNull AdjectiveForm adjectiveForm)
    {
        checkNotNull(adjectiveForm);
        requireAllowedComparisonDegree(adjectiveForm.getComparisonDegree());
        return adjectiveForm;
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
}
