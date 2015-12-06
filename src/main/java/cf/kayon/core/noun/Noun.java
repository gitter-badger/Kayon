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
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a Noun.
 * <p>
 * Information about property change listeners:
 * <table summary="">
 * <thead>
 * <tr>
 * <td>Property Name</td>
 * <td>Fired by</td>
 * <td>Triggers</td>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>{@code $CASE_$COUNT_defined}</td>
 * <td>{@link #setDefinedForm(NounForm, String)}</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@code $CASE_$COUNT_declined}</td>
 * <td>{@link #_declineIntoBuffer()}</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@code rootWord}</td>
 * <td>{@link #setRootWord(String)}</td>
 * <td>{@link #_declineIntoBuffer()}</td>
 * </tr>
 * <tr>
 * <td>{@code nounDeclension}</td>
 * <td>{@link #setNounDeclension(NounDeclension)}</td>
 * <td>{@link #_declineIntoBuffer()}</td>
 * </tr>
 * <tr>
 * <td>{@code gender}</td>
 * <td>{@link #setGender(Gender)}</td>
 * <td>{@link #_declineIntoBuffer()}</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
@ThreadSafe
public class Noun extends StandardVocab implements DeepCopyable<Noun>
{
    //region Fields
    /**
     * The declined forms of this Noun.
     *
     * @since 0.2.0
     */
    @NotNull
    private final ConcurrentMap<NounForm, String> declinedForms = new ConcurrentHashMap<>(12);

    /**
     * The defined forms of this noun.
     *
     * @since 0.0.1
     */
    @NotNull
    private final ConcurrentMap<NounForm, String> definedForms = new ConcurrentHashMap<>(12);

    /**
     * The gender of this noun.
     *
     * @since 0.0.1
     */
    @GuardedBy("this")
    @NotNull
    private Gender gender;

    /**
     * The root word of this noun.
     *
     * @since 0.0.1
     */
    @GuardedBy("this")
    @NotNull
    private String rootWord;

    /**
     * The NounDeclension of this noun. {@code null} if this noun does not have a NounDeclension.
     *
     * @since 0.0.1
     */
    @GuardedBy("this")
    @Nullable
    private NounDeclension nounDeclension;

    {
        addPropertyChangeListener(evt -> {
            String propertyName = evt.getPropertyName();
            if (propertyName.equals("gender") || propertyName.equals("rootWord") || propertyName.equals("nounDeclension") || propertyName.endsWith("_defined"))
                _declineIntoBuffer();
        });
    }

    /**
     * Constructs a new Noun.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this constructor.
     *
     * @param context        The {@link KayonContext} for this instance.
     * @param nounDeclension The noun declension of the new noun. {@code null} if there is no NounDeclension.
     * @param gender         The gender.
     * @param rootWord       The root word.
     * @throws NullPointerException     If {@code context}, {@code gender} or {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @implNote The constructor itself does not use property change support. All arguments are validated with the default constraints
     * and afterwards {@link #_declineIntoBuffer()} is called.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Noun(@NotNull KayonContext context, @Nullable NounDeclension nounDeclension, @NotNull Gender gender, @NotNull String rootWord)
    {
        super(context);
        checkNotNull(gender);
        checkNotEmpty(rootWord);
        synchronized (this)
        {
            this.nounDeclension = nounDeclension;
            this.gender = gender;
            this.rootWord = rootWord;
            _declineIntoBuffer();
        }
    }

    /**
     * Constructs a new Noun with no NounDeclension.
     * This is equal to calling {@link #Noun(KayonContext, NounDeclension, Gender, String)} with {@code null} as its first parameter.
     * <p>
     * Implementation note: The constructor itself does not use property change support. All arguments are validated with the default constraints
     * and afterwards {@link #_declineIntoBuffer()} is called.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param context  The {@link KayonContext} for this instance.
     * @param gender   The gender.
     * @param rootWord The root word.
     * @throws NullPointerException     If {@code context}, {@code gender} or {@code rootWord} is {@code null}.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public Noun(@NotNull KayonContext context, @NotNull Gender gender, @NotNull String rootWord)
    {
        this(context, null, gender, rootWord);
    }

    /**
     * Defines a form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code should only apply lowercase forms to this method.
     *
     * @param nounForm The noun form.
     * @param form     The form. If the form is {@code null} or is {@link String#isEmpty() empty}, the defined forms is instead removed.
     *                 A note about property change listeners: empty strings get converted to nulls before they are passed to listeners.
     *                 A removal of a form is represented by a new value of {@code null}.
     * @throws NullPointerException If {@code nounForm} is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public void setDefinedForm(@NotNull NounForm nounForm, @Nullable String form)
    {
        checkNotNull(nounForm);
        if (form == null || form.isEmpty())
            getPropertyChangeSupport().firePropertyChange(nounForm.getPropertyName("defined"), definedForms.remove(nounForm), form);
        else
            getPropertyChangeSupport().firePropertyChange(nounForm.getPropertyName("defined"), definedForms.put(nounForm, form), form);
    }

    /**
     * Gets a defined form.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param nounForm The noun form.
     * @return The form, as it has been defined. {@code null} if the form has not been defined.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDefinedForm(@NotNull NounForm nounForm)
    {
        checkNotNull(nounForm);
        return definedForms.get(nounForm);
    }

    /**
     * Removes a defined form.
     * <p>
     * This is exactly equal to calling {@link #setDefinedForm(NounForm, String)} with {@code null} as its third argument.
     * <p>
     * See {@link #setDefinedForm(NounForm, String)} for more information on property change listeners.
     *
     * @param nounForm The noun form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    public void removeDefinedForm(@NotNull NounForm nounForm)
    {
        setDefinedForm(nounForm, null);
    }

    /**
     * Gets a form, as it has been declined by the underlying {@link NounDeclension}.
     * <p>
     * The general contract of this class is to only contain lowercase forms (see annotation).
     * Code using this method may rely on this contract.
     *
     * @param nounForm The noun form.
     * @return The form. {@code null} if the underlying {@link NounDeclension} could not determine the form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @Nullable
    public String getDeclinedForm(@NotNull NounForm nounForm)
    {
        checkNotNull(nounForm);
        return this.declinedForms.get(nounForm);
    }

    /**
     * Gets a form - defined or declined (defined takes precedence).
     *
     * @param nounForm The noun form.
     * @return The form. {@code null} if there is both no defined or declined form.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.0.1
     */
    @Nullable
    public synchronized String getForm(@NotNull NounForm nounForm)
    {
        @Nullable
        String definedFormOrNull = getDefinedForm(nounForm); // Delegates NotNull
        if (definedFormOrNull != null)
            return definedFormOrNull;
        return getDeclinedForm(nounForm);
    }

    /**
     * Called if changes to declined form changing properties occur.
     * <p>
     * Declines all declined forms into the buffer.
     *
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    private synchronized void _declineIntoBuffer()
    {
        for (NounForm nounForm : NounForm.values())
            if (this.nounDeclension != null)
            {
                String newValue;
                try
                {
                    newValue = nounDeclension.decline(nounForm, this.gender, this.rootWord);
                } catch (FormingException e)
                {
                    newValue = null;
                }
                getPropertyChangeSupport().firePropertyChange(nounForm.getPropertyName("declined"),
                                                              newValue != null ? this.declinedForms.put(nounForm, newValue) : this.declinedForms.remove(nounForm),
                                                              newValue);
            } else
                getPropertyChangeSupport().firePropertyChange(nounForm.getPropertyName("declined"), this.declinedForms.remove(nounForm), null);
    }

    /**
     * Gets the gender of this noun.
     *
     * @return The gender. Never null.
     * @since 0.0.1
     */
    @NotNull
    public synchronized Gender getGender()
    {
        return gender;
    }

    /**
     * Sets the gender of this noun.
     *
     * @param gender The new gender.
     * @since 0.0.1
     */
    public synchronized void setGender(@NotNull Gender gender)
    {
        Gender oldGender = this.gender;
        this.gender = gender;
        getPropertyChangeSupport().firePropertyChange("gender", oldGender, gender);
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
    public synchronized String getRootWord()
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
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    public synchronized void setRootWord(@NotNull String rootWord)
    {
        checkNotEmpty(rootWord);
        String oldRootWord = this.rootWord;
        this.rootWord = rootWord;
        getPropertyChangeSupport().firePropertyChange("rootWord", oldRootWord, rootWord);
    }

    /**
     * Gets the NounDeclension of this class.
     *
     * @return The NounDeclension. {@code null} if this noun does not have a noun declension.
     * @since 0.0.1
     */
    @Nullable
    public synchronized NounDeclension getNounDeclension()
    {
        return this.nounDeclension;
    }

    /**
     * Sets the NounDeclension of this class.
     *
     * @param nounDeclension The new NounDeclension. {@code null} if the noun should not have a noun declension.
     * @since 0.0.1
     */
    public synchronized void setNounDeclension(@Nullable NounDeclension nounDeclension)
    {
        NounDeclension oldNounDeclension = this.nounDeclension;
        this.nounDeclension = nounDeclension;
        getPropertyChangeSupport().firePropertyChange("nounDeclension", oldNounDeclension, nounDeclension);
    }

    /**
     * @implNote The PropertyChangeListeners of the two classes do not need to equal to consider the two objects equal.
     */
    @Override
    public synchronized boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Noun)) return false;
        if (!super.equals(o)) return false;
        Noun noun = (Noun) o;
        return Objects.equal(declinedForms, noun.declinedForms) &&
               Objects.equal(definedForms, noun.definedForms) &&
               gender == noun.gender &&
               Objects.equal(rootWord, noun.rootWord) &&
               Objects.equal(nounDeclension, noun.nounDeclension);

    }

    @Override
    public synchronized int hashCode()
    {
        return Objects.hashCode(super.hashCode(), declinedForms, definedForms, gender, rootWord, nounDeclension);
    }

    /**
     * @implNote This will also copy the UUID over, if it exists (the resulting object will have the same UUID as this one).
     * PropertyChangeListeners will <strong>not</strong> be copied.
     * @since 0.2.0
     */
    @NotNull
    @Override
    public synchronized Noun copyDeep()
    {
        // Noun Declension, Gender, root word (all immutable)
        Noun noun = new Noun(getContext(), this.nounDeclension, this.gender, this.rootWord);

        // Defined forms (NounForm and String are immutable)
        noun.definedForms.putAll(this.definedForms);

        // UUID (immutable)
        UUID uuid = this.getUuid();
        if (uuid != null)
            noun.initializeUuid(uuid);

        // Translations (Locale and String are immutable)
        noun.setTranslations(new HashMap<>(this.getTranslations()));

        // Declined forms refresh
        noun._declineIntoBuffer();

        return noun;
    }

    @Override
    public synchronized String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("declinedForms", declinedForms)
                          .add("definedForms", definedForms)
                          .add("gender", gender)
                          .add("rootWord", rootWord)
                          .add("nounDeclension", nounDeclension)
                          .toString();
    }
}
