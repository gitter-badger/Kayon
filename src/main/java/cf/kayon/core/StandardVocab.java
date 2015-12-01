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

package cf.kayon.core;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a standard implementation for all vocab classes.
 *
 * @author Ruben Anders
 * @since 0.2.0
 */
public class StandardVocab extends Contexed implements Vocab
{
    /**
     * The UUID of this StandardVocab.
     *
     * @since 0.2.0
     */
    private UUID uuid;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof StandardVocab)) return false;
        if (!super.equals(o)) return false;
        StandardVocab vocab = (StandardVocab) o;
        return Objects.equal(uuid, vocab.uuid) &&
               Objects.equal(translations, vocab.translations);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(super.hashCode(), uuid, translations);
    }

    /**
     * Instantiates a new StandardVocab.
     *
     * @param context The {@link KayonContext} for this instance.
     * @since 0.2.0
     */
    protected StandardVocab(@NotNull KayonContext context)
    {

        super(context);
    }

    /**
     * @since 0.2.0
     */
    @Nullable
    @Override
    public UUID getUuid()
    {
        return uuid;
    }

    /**
     * @since 0.2.0
     */
    @Override
    public void initializeUuid(@NotNull UUID uuid)
    {
        checkNotNull(uuid);
        if (this.uuid != null)
            throw new IllegalStateException("UUID has already been initialized!");
        this.uuid = uuid;
        changeSupport.firePropertyChange("uuid", null, uuid);
    }

    /**
     * The translation storage of this StandardVocab.
     *
     * @since 0.2.0
     */
    private Map<Locale, String> translations = new HashMap<>();

    /**
     * The ResourceBundle.Control instance used to get candidate locales.
     * <p>
     * Actually, these control instances were not made for this job (they are supposed to operate with properties files or classes,
     * but it's possible to never bring them into context of resource bundles and simply use their utility methods, as it is done here.
     *
     * @since 0.2.0
     */
    private static final ResourceBundle.Control CONTROL = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    /**
     * @since 0.2.0
     */
    @NotNull
    @Override
    public Map<Locale, String> getTranslations()
    {
        return translations;
    }

    /**
     * @since 0.2.0
     */
    @Nullable
    @Override
    public String getTranslation(@NotNull Locale locale)
    {
        List<Locale> localeCandidates = CONTROL.getCandidateLocales("_dummy_", locale); // Sun's implementation discards the string argument
        for (Locale currentCandidate : localeCandidates)
        {
            String translation = translations.get(currentCandidate);
            if (translation != null)
                return translation;
        }
        return null;
    }

    /**
     * @since 0.2.0
     */
    @Override
    public void setTranslations(@NotNull Map<Locale, String> map)
    {
        this.translations = map;
    }

    /**
     * The PropertyChangeSupport for this class.
     *
     * @since 0.2.0
     */
    @NotNull
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this); // protected for usage in subclasses

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

}
