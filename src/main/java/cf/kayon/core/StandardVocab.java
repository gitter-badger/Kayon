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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a standard implementation for all vocab classes.
 *
 * @author Ruben Anders
 * @since 0.2.0
 */
@ThreadSafe
public class StandardVocab extends Contexed implements Vocab
{
    /**
     * The UUID of this StandardVocab.
     *
     * @since 0.2.0
     */
    @GuardedBy("uuidLock")
    private UUID uuid;

    /**
     * The guard object for accessing the {@link #uuid UUID field}.
     *
     * @since 0.2.0
     */
    private final Object uuidLock = new Object();

    /**
     * @since 0.2.0
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof StandardVocab)) return false;
        if (!super.equals(o)) return false;
        StandardVocab vocab = (StandardVocab) o;
        synchronized (uuidLock)
        {
            //noinspection NestedSynchronizedStatement
            synchronized (translations)
            {
                return Objects.equal(uuid, vocab.uuid) &&
                       Objects.equal(translations, vocab.translations);
            }
        }
    }

    /**
     * @since 0.2.0
     */
    @Override
    public int hashCode()
    {
        synchronized (uuidLock)
        {
            //noinspection NestedSynchronizedStatement
            synchronized (translations)
            {
                return Objects.hashCode(super.hashCode(), uuid, translations);
            }
        }
    }

    /**
     * Instantiates a new StandardVocab.
     * <p>
     * This constructor is {@code protected} so new instances of this class can only be created
     * by classes extending this one.
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
        synchronized (uuidLock)
        {
            return uuid;
        }
    }

    /**
     * @since 0.2.0
     */
    @Override
    public void initializeUuid(@NotNull UUID uuid)
    {
        checkNotNull(uuid);
        synchronized (uuidLock)
        {
            if (this.uuid != null)
                throw new IllegalStateException("UUID has already been initialized!");
            this.uuid = uuid;
        }
        getPropertyChangeSupport().firePropertyChange("uuid", null, uuid);
    }

    /**
     * The translation storage of this StandardVocab.
     *
     * @since 0.2.0
     */
    @GuardedBy("translations")
    private final HashMap<Locale, String> translations = new HashMap<>();

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
        synchronized (translations)
        {
            return translations;
        }
    }

    /**
     * @since 0.2.0
     */
    @Nullable
    @Override
    public String getTranslation(@NotNull Locale locale)
    {
        checkNotNull(locale);
        List<Locale> localeCandidates = CONTROL.getCandidateLocales("_dummy_", locale); // Sun's implementation discards the string argument
        synchronized (translations)
        {
            for (Locale currentCandidate : localeCandidates)
            {
                String translation = translations.get(currentCandidate);
                if (translation != null)
                    return translation;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Implementation note:</strong> The map instance will not be set in this StandardVocab, instead the map's contents will copied into the own map after the own map has been cleared.
     *
     * @since 0.2.0
     */
    @Override
    public void setTranslations(@NotNull Map<Locale, String> map)
    {
        checkNotNull(map);
        synchronized (translations) // race condition
        {
            translations.clear();
            translations.putAll(map); // Locale and string are immutable
        }
    }

    @SuppressWarnings("FieldNotUsedInToString")
    @NotNull
    @GuardedBy("changeSupport")
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

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
     * @since 0.2.0
     */
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener)
    {
        checkNotNull(listener);
        synchronized (changeSupport)
        {
            changeSupport.addPropertyChangeListener(listener);
        }
    }

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If {@code propertyName} or {@code listener} is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The PropertyChangeListener to be added
     * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     * @since 0.2.0
     */
    public void addPropertyChangeListener(@NotNull String propertyName, @NotNull PropertyChangeListener listener)
    {
        checkNotEmpty(propertyName);
        checkNotNull(listener);
        synchronized (changeSupport)
        {
            changeSupport.addPropertyChangeListener(propertyName, listener);
        }
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * If {@code listener} was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     * If {@code propertyName} is null,  no exception is thrown and no
     * action is taken.
     * If {@code listener} is null, or was never added for the specified
     * property, no exception is thrown and no action is taken.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The PropertyChangeListener to be removed
     * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     * @since 0.2.0
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        checkNotEmpty(propertyName);
        checkNotNull(listener);
        synchronized (changeSupport)
        {
            changeSupport.removePropertyChangeListener(propertyName, listener);
        }
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
     * @since 0.2.0
     */
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener)
    {
        checkNotNull(listener);
        synchronized (changeSupport)
        {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Gets the PropertyChangeSupport for this class.
     * <p>
     * This method is {@code protected} so it can be accessed from any classes extending this class (for example for firing events). Code willing to add/remove listeners
     * to this object should use {@link #addPropertyChangeListener(PropertyChangeListener)} and {@link #removePropertyChangeListener(PropertyChangeListener)}.
     *
     * @return The PropertyChangeSupport of this StandardVocab.
     * @since 0.2.0
     */
    @NotNull
    protected PropertyChangeSupport getPropertyChangeSupport()
    {
        synchronized (changeSupport)
        {
            return changeSupport;
        }
    }

    /**
     * @since 0.2.0
     */
    @Override
    public String toString()
    {
        synchronized (uuidLock)
        {
            //noinspection NestedSynchronizedStatement
            synchronized (translations)
            {
                return MoreObjects.toStringHelper(this)
                                  .add("uuid", uuid)
                                  .add("translations", translations)
                                  .toString();
            }
        }
    }
}
