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
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
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
 * <td>Methods called on this event</td>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>{@code $COMPARISONDEGREE_allowed}</td>
 * <td>{@link #setAllows(ComparisonDegree, boolean)}, {@link #setAllowsPositive(boolean)},
 * {@link #setAllowsComparative(boolean)}, {@link #setAllowsSuperlative(boolean)}</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@code $COMPARISONDEGREE_$CASE_$COUNT_$GENDER_defined}</td>
 * <td>{@link #setDefinedForm(AdjectiveForm, String)}</td>
 * <td>{@link #_declineIntoBuffer()}</td>
 * </tr>
 * <tr>
 * <td>{@code $COMPARISONDEGREE_$CASE_$COUNT_$GENDER_declined}</td>
 * <td>{@link #_declineIntoBuffer()}</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@code adjectiveDeclension}</td>
 * <td>{@link #setAdjectiveDeclension(AdjectiveDeclension)}</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@code rootWord}</td>
 * <td>{@link #setRootWord(String)}</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@code uuid}</td>
 * <td>{@link #initializeUuid(UUID)}</td>
 * <td></td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class Adjective extends StandardVocab implements DeepCopyable<Adjective>
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
     * @param context             The {@link KayonContext} for this instance.
     * @param adjectiveDeclension An AdjectiveDeclension or {@code null}.
     * @param rootWord            The root word of the adjective.
     * @throws NullPointerException     If {@code rootWord} or {@code context} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Adjective(@NotNull KayonContext context, @Nullable AdjectiveDeclension adjectiveDeclension, @NotNull String rootWord)
    {
        super(context);
        checkNotEmpty(rootWord);
        this.adjectiveDeclension = adjectiveDeclension;
        this.rootWord = rootWord;
        _declineIntoBuffer(); // speed improvement
    }

    /**
     * Constructs a new Adjective with no AdjectiveDeclension.
     * <p>
     * The {@code rootWord} argument should be lowercase only (see annotation).
     *
     * @param context The {@link KayonContext} for this instance.
     * @param rootWord The root word of the adjective.
     * @throws NullPointerException     If {@code rootWord} or {@code context} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Adjective(@NotNull KayonContext context, @NotNull String rootWord)
    {
        this(context, null, rootWord);
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
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void setRootWord(@NotNull String rootWord)
    {
        checkNotEmpty(rootWord);
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
     * @since 0.0.1
     */
    public void setAdjectiveDeclension(@Nullable AdjectiveDeclension adjectiveDeclension)
    {
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
     * @see #setAllows(ComparisonDegree, boolean)
     * @since 0.0.1
     */
    public void setAllowsPositive(boolean allowsPositive)
    {
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
     * @see #setAllows(ComparisonDegree, boolean)
     * @since 0.0.1
     */
    public void setAllowsComparative(boolean allowsComparative)
    {
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
     * @see #setAllows(ComparisonDegree, boolean)
     * @since 0.0.1
     */
    public void setAllowsSuperlative(boolean allowsSuperlative)
    {
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
                throw new RuntimeException();
        }
    }

    /**
     * Sets whether this adjective allows the specified comparison degree.
     *
     * @param comparisonDegree The comparison degree to check for.
     * @param allows           Whether to allow or disallow.
     * @see #setAllowsPositive(boolean)
     * @see #setAllowsComparative(boolean)
     * @see #setAllowsSuperlative(boolean)
     * @since 0.0.1
     */
    public void setAllows(@NotNull ComparisonDegree comparisonDegree, boolean allows)
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
            default:
                throw new RuntimeException();
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
     * @throws NullPointerException     If {@code adjectiveForm} is {@code null}.
     * @throws IllegalArgumentException If the {@code comparisonDegree} is {@link #allows(ComparisonDegree) disallowed}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void setDefinedForm(@NotNull AdjectiveForm adjectiveForm, @Nullable String form)
    {
        requireAllowedComparisonDegree(adjectiveForm);
        checkNotNull(adjectiveForm);

        if (form == null || form.isEmpty())
        {
            // Fires its own events
            removeDefinedForm(adjectiveForm);
            return;
        }

        String oldForm = getDefinedForm(adjectiveForm);
        String propertyName = adjectiveForm.getPropertyName("defined");

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adjective adjective = (Adjective) o;
        return allowsPositive == adjective.allowsPositive &&
               allowsComparative == adjective.allowsComparative &&
               allowsSuperlative == adjective.allowsSuperlative &&
               Objects.equal(declinedForms, adjective.declinedForms) &&
               Objects.equal(definedForms, adjective.definedForms) &&
               Objects.equal(rootWord, adjective.rootWord) &&
               Objects.equal(adjectiveDeclension, adjective.adjectiveDeclension);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(declinedForms, definedForms, rootWord, adjectiveDeclension, allowsPositive, allowsComparative, allowsSuperlative);
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
     * @throws NullPointerException If {@code adjectiveForm} is {@code null}.
     * @since 0.0.1
     */
    public void removeDefinedForm(@NotNull AdjectiveForm adjectiveForm)
    {
        requireAllowedComparisonDegree(adjectiveForm);
        checkNotNull(adjectiveForm);

        String propertyName =
                adjectiveForm.getComparisonDegree() + "_" + adjectiveForm.getCase() + "_" + adjectiveForm.getCount() + "_" + adjectiveForm.getGender() + "_defined";
        String oldForm = definedForms.get(adjectiveForm);

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
        String propertyName = adjectiveForm.getPropertyName("declined");
        if (form == null || form.isEmpty())
        {
            declinedForms.remove(adjectiveForm);
            changeSupport.firePropertyChange(propertyName, oldForm, null);
        } else
        {
            declinedForms.put(adjectiveForm, form);
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
        { // Properly invoke all change listeners
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
    }

    /**
     * This will also copy the UUID over, if it exists (the resulting object will have the same UUID as this one).
     * <p>
     * PropertyChangeListeners will <strong>not</strong> be copied.
     * {@inheritDoc}
     *
     * @since 0.2.0
     */
    @NotNull
    @Override
    public Adjective copyDeep()
    {
        // Adjective Declension and root word (both immutable)
        Adjective adjective = new Adjective(getContext(), this.adjectiveDeclension, this.rootWord);

        // Copy defined forms map (AdjectiveForm and String are immutable)
        adjective.definedForms.putAll(this.definedForms);

        // UUID (immutable)
        UUID uuid = this.getUuid();
        if (uuid != null)
            adjective.initializeUuid(uuid);

        // Translations (Locale and String are immutable)
        adjective.setTranslations(new HashMap<>(this.getTranslations()));

        adjective.setAllowsPositive(this.allowsPositive);
        adjective.setAllowsComparative(this.allowsComparative);
        adjective.setAllowsSuperlative(this.allowsSuperlative);

        adjective._declineIntoBuffer();

        return adjective;
    }
}
