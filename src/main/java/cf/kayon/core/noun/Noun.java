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

import cf.kayon.core.*;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cf.kayon.core.util.StringUtil.requireNonEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a Noun.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class Noun implements Vocab
{
    //region Fields
    /**
     * The declined forms of this Noun.
     *
     * @since 0.0.1
     */
    @NotNull
    private final ArrayTable<Case, Count, String> declinedForms = ArrayTable.create(
            () -> new ObjectArrayIterator<>(Case.values()),
            () -> new ObjectArrayIterator<>(Count.values()));

    /**
     * The defined forms of this noun.
     *
     * @since 0.0.1
     */
    @NotNull
    private final Table<Case, Count, String> definedForms = HashBasedTable.create(6, 2);

    /**
     * The translations of this noun.
     *
     * @since 0.0.1
     */
    @NotNull
    private Map<String, String> translations = Maps.newHashMap();

    /**
     * The gender of this noun.
     *
     * @since 0.0.1
     */
    @NotNull
    private Gender gender;

    /**
     * The root word of this noun.
     *
     * @since 0.0.1
     */
    @NotNull
    private String rootWord;

    /**
     * The NounDeclension of this noun. {@code null} if this noun does not have a NounDeclension.
     *
     * @since 0.0.1
     */
    @Nullable
    private NounDeclension nounDeclension;

    /**
     * The PropertyChangeSupport of this class.
     * <table summary="">
     * <thead>
     * <tr>
     * <td>Property Name</td>
     * <td>Fired by</td>
     * <td>Vetoable?</td>
     * <td>Triggers</td>
     * <td>Default checks</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>{@code $CASE_$COUNT_defined}</td>
     * <td>{@link #setDefinedForm(Case, Count, String)}</td>
     * <td>Yes</td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code $CASE_$COUNT_declined}</td>
     * <td>{@link #_declineIntoBuffer()}</td>
     * <td>No</td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code rootWord}</td>
     * <td>{@link #setRootWord(String)}</td>
     * <td>Yes</td>
     * <td>{@link #_declineIntoBuffer()}</td>
     * <td>Not {@code null}, not {@link String#isEmpty() empty}</td>
     * </tr>
     * <tr>
     * <td>{@code nounDeclension}</td>
     * <td>{@link #setNounDeclension(NounDeclension)}</td>
     * <td>Yes</td>
     * <td>{@link #_declineIntoBuffer()}</td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>{@code gender}</td>
     * <td>{@link #setGender(Gender)}</td>
     * <td>Yes</td>
     * <td>{@link #_declineIntoBuffer()}</td>
     * <td>Not {@code null}</td>
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
     * See {@link #changeSupport} for more details on triggered property changes.
     *
     * @since 0.0.1
     */
    @NotNull
    private final VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);
    //endregion

    //region Constructors

    /**
     * Constructs a new Noun.
     * <p>
     * Note: The constructor itself does not use property/vetoable change support. All arguments are validated with the default constraints
     * and afterwards {@link #_declineIntoBuffer()} is called.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param nounDeclension The noun declension of the new noun. {@code null} if there is no NounDeclension.
     * @param gender         The gender.
     * @param rootWord       The root word.
     * @throws NullPointerException     If {@code gender} or {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Noun(@Nullable NounDeclension nounDeclension, @NotNull Gender gender, @NotNull String rootWord)
    {
        checkNotNull(gender);
        requireNonEmpty(rootWord);
        this.nounDeclension = nounDeclension;
        this.gender = gender;
        this.rootWord = rootWord;
        _declineIntoBuffer();
    }

    /**
     * @since 0.0.1
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Noun noun = (Noun) o;
        return Objects.equal(declinedForms, noun.declinedForms) &&
               Objects.equal(definedForms, noun.definedForms) &&
               Objects.equal(translations, noun.translations) &&
               Objects.equal(gender, noun.gender) &&
               Objects.equal(rootWord, noun.rootWord) &&
               Objects.equal(nounDeclension, noun.nounDeclension) &&
               Objects.equal(uuid, noun.uuid);
    }

    /**
     * @since 0.0.1
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(declinedForms, definedForms, translations, gender, rootWord, nounDeclension, uuid);
    }

    /**
     * Constructs a new Noun with no NounDeclension.
     * This is equal to calling {@link #Noun(NounDeclension, Gender, String)} with {@code null} as its first parameter.
     * <p>
     * Note: The constructor itself does not use property/vetoable change support. All arguments are validated with the default constraints
     * and afterwards {@link #_declineIntoBuffer()} is called.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param gender   The gender.
     * @param rootWord The root word.
     * @throws NullPointerException     If {@code gender} or {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Noun(@NotNull Gender gender, @NotNull String rootWord)
    {
        this(null, gender, rootWord);
    }
    //endregion

    //region Defining forms

    /**
     * Defines a form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param caze  The case.
     * @param count The count.
     * @param form  The form. If the form is {@code null} or is {@link String#isEmpty() empty}, the defined forms is instead removed.
     *              A note about change/veto listeners: empty strings get converted to nulls before they are passed to listeners.
     *              A removal of a form is represented by a new value of {@code null}.
     * @throws NullPointerException  If {@code caze} or {@code count} is {@code null}.
     * @throws PropertyVetoException If the {@link #vetoSupport} of this class decides that the new value is not valid.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void setDefinedForm(@NotNull Case caze, @NotNull Count count, @Nullable String form) throws PropertyVetoException
    {
        String oldForm = getDefinedForm(caze, count);
        if (form != null && form.isEmpty())
            form = null; // empty strings get converted to nulls
        vetoSupport.fireVetoableChange(caze + "_" + count + "_defined", oldForm, form);
        if (form != null)
            definedForms.put(caze, count, form);
        else
            definedForms.remove(caze, count);
        changeSupport.firePropertyChange(caze + "_" + count + "_defined", oldForm, form);
    }

    /**
     * Gets a defined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param caze  The case.
     * @param count The count.
     * @return The form, as it has been defined. {@code null} if the form has not been defined.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDefinedForm(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return definedForms.get(caze, count);
    }

    /**
     * Removes a defined form.
     * <p>
     * This is exactly equal to calling {@link #setDefinedForm(Case, Count, String)} with {@code null} as its third argument.
     * <p>
     * See {@link #setDefinedForm(Case, Count, String)} for more information on property change/veto listeners.
     *
     * @param caze  The case.
     * @param count The count.
     * @throws PropertyVetoException If the {@link #vetoSupport} of this class decides that the new value {@code null} is not valid.
     * @throws NullPointerException  If any of the arguments if {@code null}.
     * @since 0.0.1
     */
    public void removeDefinedForm(@NotNull Case caze, @NotNull Count count) throws PropertyVetoException
    {
        setDefinedForm(caze, count, null);
    }
    //endregion

    //region Declining forms

    /**
     * Gets a form, as it has been declined by the underlying {@link NounDeclension}.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param caze  The case.
     * @param count The count.
     * @return The form. {@code null} if the underlying {@link NounDeclension} could not determine the form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDeclinedForm(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return this.declinedForms.get(caze, count);
    }

    /**
     * Called if changes to declined form changing properties occur.
     * <p>
     * Declines all declined forms into the buffer.
     *
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private void _declineIntoBuffer()
    {
        for (Case caze : Case.values())
            for (Count count : Count.values())
            {
                String oldValue = this.declinedForms.get(caze, count);
                String newValue;
                if (this.getNounDeclension() == null)
                    newValue = null;
                else
                    try
                    {
                        newValue = getNounDeclension().decline(caze, count, this.gender, this.rootWord);
                    } catch (FormingException ignored)
                    {
                        newValue = null;
                    }
                this.declinedForms.put(caze, count, newValue);
                changeSupport.firePropertyChange(caze + "_" + count + "_declined", oldValue, newValue);
            }
    }
    //endregion

    //region Setters and Getters

    /**
     * Gets the gender of this noun.
     *
     * @return The gender. Never null.
     * @since 0.0.1
     */
    @NotNull
    public Gender getGender()
    {
        return gender;
    }

    /**
     * Sets the gender of this noun.
     *
     * @param gender The new gender.
     * @throws PropertyVetoException If the {@link #vetoSupport} of this class decides that the new value is invalid.
     * @since 0.0.1
     */
    public void setGender(@NotNull Gender gender) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("gender", this.gender, gender);
        Gender oldGender = this.gender;
        this.gender = gender;
        changeSupport.firePropertyChange("gender", oldGender, gender);
    }

    /**
     * Gets the root word.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @return The root word.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    public String getRootWord()
    {
        return rootWord;
    }

    /**
     * Sets the root word.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param rootWord The new root word.
     * @throws PropertyVetoException If the {@link #vetoSupport} of this class decides that the new value is invalid.
     * @since 0.0.1
     */
    public void setRootWord(@NotNull String rootWord) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("rootWord", this.rootWord, rootWord);
        String oldRootWord = this.rootWord;
        this.rootWord = rootWord;
        changeSupport.firePropertyChange("rootWord", oldRootWord, rootWord);
    }

    /**
     * Gets the NounDeclension of this class.
     *
     * @return The NounDeclension. {@code null} if this noun does not have a noun declension.
     * @since 0.0.1
     */
    @Nullable
    public NounDeclension getNounDeclension()
    {
        return this.nounDeclension;
    }

    /**
     * Sets the NounDeclension of this class.
     *
     * @param nounDeclension The new NounDeclension. {@code null} if the noun should not have a noun declension.
     * @throws PropertyVetoException If the {@link #vetoSupport} of this class decides that the new value is invalid.
     * @since 0.0.1
     */
    public void setNounDeclension(@Nullable NounDeclension nounDeclension) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("nounDeclension", this.nounDeclension, nounDeclension);
        NounDeclension oldNounDeclension = this.nounDeclension;
        this.nounDeclension = nounDeclension;
        changeSupport.firePropertyChange("nounDeclension", oldNounDeclension, nounDeclension);
    }
    //endregion

    //region Translation

    /**
     * @since 0.0.1
     */
    @NotNull
    public Map<String, String> getTranslations()
    {
        return translations;
    }

    /**
     * Sets the translations of this noun.
     *
     * @param translations The translations map to set.
     * @since 0.0.1
     */
    public void setTranslations(@NotNull Map<String, String> translations)
    {
        checkNotNull(translations);
        this.translations = translations;
    }

    /**
     * Gets a form - defined or declined - which one is present (defined takes precedence).
     *
     * @param caze  The case.
     * @param count The count.
     * @return The form. {@code null} if there is both no defined or declined form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @Nullable
    public String getForm(@NotNull Case caze, @NotNull Count count)
    {
        @Nullable
        String definedFormOrNull = getDefinedForm(caze, count); // Delegates NotNull
        if (definedFormOrNull != null)
            return definedFormOrNull;
        return getDeclinedForm(caze, count);
    }
    //endregion

    /**
     * @since 0.0.1
     * @deprecated Use the JavaFX graphical interface instead. Scheduled for removal as of 0.1.0.
     */
    @NotNull
    @Override
    @Deprecated
    public List<String> commandLineRepresentation()
    {
        List<String> buffer = new ArrayList<>(10);
        buffer.add("+----------+----------------+----------------+");
        buffer.add("|          |    SINGULAR    |      PLURAL    |");
        buffer.add("+----------+----------------+----------------+");
        for (Case caze : Case.values())
        {
            StringBuilder sB = new StringBuilder(46);
            sB.append("|");
            sB.append(Strings.padStart(caze.toString(), 10, ' '));
            sB.append("|");

            sB.append(definedForms.contains(caze, Count.SINGULAR) ? '$' : ' ');
            @Nullable
            String singularFormOrNull = getForm(caze, Count.SINGULAR);
            singularFormOrNull = singularFormOrNull == null ? "???????????????" : Strings.padEnd(singularFormOrNull, 15, ' ');
            sB.append(singularFormOrNull);

            sB.append("|");

            sB.append(definedForms.contains(caze, Count.PLURAL) ? '$' : ' ');
            @Nullable
            String pluralFormOrNull = getForm(caze, Count.PLURAL);
            pluralFormOrNull = pluralFormOrNull == null ? "???????????????" : Strings.padEnd(pluralFormOrNull, 15, ' ');
            sB.append(pluralFormOrNull);

            sB.append("|");

            buffer.add(sB.toString());
        }

        buffer.add("+----------+----------------+----------------+");

        return buffer;
    }

    //region UUID
    /**
     * The UUID of this noun.
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
    //endregion

    //region Bean support
    {
        addPropertyChangeListener(evt -> {
            String propertyName = evt.getPropertyName();
            if (propertyName.equals("gender") || propertyName.equals("rootWord") || propertyName.equals("nounDeclension") || propertyName.endsWith("_defined"))
                _declineIntoBuffer();
        });

        addVetoableChangeListener(evt -> {
            if (!evt.getPropertyName().endsWith("defined"))
            {
                Object newValue = evt.getNewValue();
                if (newValue == null)
                    throw new PropertyVetoException("New value may not be null!", evt);
                if (newValue instanceof String && ((String) newValue).isEmpty())
                    throw new PropertyVetoException("New String value may not be empty!", evt);
            }
        });
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If {@code listener} is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener The PropertyChangeListener to be added
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     * @since 0.0.1
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     * If {@code listener} was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If {@code listener} is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener The PropertyChangeListener to be removed
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     * @since 0.0.1
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Add a VetoableChangeListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If {@code listener} is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener The VetoableChangeListener to be added
     * @see VetoableChangeSupport#addVetoableChangeListener(VetoableChangeListener)
     * @since 0.0.1
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
        vetoSupport.addVetoableChangeListener(listener);
    }

    /**
     * Remove a VetoableChangeListener from the listener list.
     * This removes a VetoableChangeListener that was registered
     * for all properties.
     * If {@code listener} was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If {@code listener} is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener The VetoableChangeListener to be removed
     * @see VetoableChangeSupport#removeVetoableChangeListener(VetoableChangeListener)
     * @since 0.0.1
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
        vetoSupport.removeVetoableChangeListener(listener);
    }
    //endregion
}
