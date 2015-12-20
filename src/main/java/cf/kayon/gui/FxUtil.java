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

package cf.kayon.gui;

import cf.kayon.core.KayonContext;
import cf.kayon.core.StandardVocab;
import javafx.beans.value.WritableValue;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static utilities to the JavaFX application.
 *
 * @author Ruben Anders
 * @since 0.2.0
 */
@ThreadSafe
public class FxUtil
{
    /**
     * The logo of the application.
     *
     * @since 0.2.0
     */
    @SuppressWarnings("HardcodedFileSeparator")
    public static final Image LOGO = new Image(FxUtil.class.getResourceAsStream("/cf/kayon/gui/logo1024.png"));

    /**
     * The KayonContext for the JavaFX application.
     *
     * @since 0.2.0
     */
    public static volatile KayonContext context;

    /**
     * The global task executor for the JavaFX application.
     *
     * @since 0.2.0
     */
    public static volatile ThreadPoolExecutor executor;

    /**
     * Adds the project's logo (up to 1024x1024) to the specified stage.
     *
     * @param stage The stage to add the icons to.
     * @since 0.2.0
     */
    public static void initLogo(@NotNull Stage stage)
    {
        checkNotNull(stage);
        stage.getIcons().add(LOGO);
    }

    /**
     * Binds a PropertyChangeListener to the StandardVocab. This PropertyChangeListener will listen to any events of the {@code String propertyName} and will
     * set the value of the {@code WritableValue<P> property}, but optionally converted to an alternating string gotten from the {@link ResourceBundle}:
     * <p>
     * If {@code bundle} is not {@code null} and {@code resourceBundleKey} is not {@code null} or {@link String#isEmpty() empty}, the value
     * of {@code property} will not be set if the new value of the listened property is {@code null}; Instead, the string from the resource bundle will
     * be set.
     * <p>
     * If {@code bundle} is {@code null} or {@code resourceBundleKey} is {@code null} or {@link String#isEmpty() empty}, the new value will always be set, even
     * if the new value is {@code null}.
     * <p>
     * See the little illustration below for a visualization of what this method does.
     * <pre>{@code
     * bundle and resourceBundleKey present? -- true --> new value == null? -- true --> set property to (T) bundle.getObject(resourceBundleKey)
     *                                       |                              |
     *                                       |                              +- false -> set property to (T) new value
     *                                       |
     *                                       +- false -> set property to (T) new value (always)
     * }</pre>
     * Note that the conversion of the new value of the event to the required type of {@code property} may lead to a {@link ClassCastException} being thrown
     * when the event listener is invoked. The caller should be careful to only bind this method to properties that can only have convertible new values.
     *
     * @param vocab             The StandardVocab to bind the PropertyChangeListener to.
     * @param property          The property to write to on events.
     * @param propertyName      The property name to listen on.
     * @param bundle            The resource bundle.
     * @param resourceBundleKey The resource bundle key.
     * @param <T>               The type of the WritableValue.
     * @return The PropertyChangeListener that was bound to the StandardVocab.
     * @since 0.2.0
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> PropertyChangeListener bindTo(@NotNull StandardVocab vocab, @NotNull WritableValue<T> property, @NotNull @NonNls
    String propertyName, @Nullable ResourceBundle bundle, @Nullable @NonNls String resourceBundleKey)
    {
        checkNotNull(vocab);
        checkNotNull(property);
        checkNotEmpty(propertyName);

        @NotNull PropertyChangeListener listener;
        if (bundle != null && resourceBundleKey != null && !resourceBundleKey.isEmpty())
        {
            listener = evt -> property.setValue(evt.getNewValue() != null ? (T) evt.getNewValue() : (T) bundle.getObject(resourceBundleKey));
        } else
        {
            listener = evt -> {
                try
                {
                    property.setValue((T) evt.getNewValue());
                } catch (ClassCastException e)
                {
                    throw (ClassCastException) new ClassCastException("Listened property " + property +
                                                                      " on vocab " + vocab +
                                                                      " got inconvertible new value " + evt.getNewValue() +
                                                                      " of type " + evt.getNewValue().getClass().getCanonicalName() +
                                                                      " (could not write to " + property + ")").initCause(e);
                }
            };
        }
        vocab.addPropertyChangeListener(propertyName, listener);
        return listener;
    }

    /**
     * Binds a PropertyChangeListener to the StandardVocab. This PropertyChangeListener will listen to any events of the {@code String propertyName} and will
     * set the value of the {@code WritableValue<P> property} as transformed by the {@code Function<E, P> transformer}.
     * <p>
     * The function is treated the following way:
     * <p>
     * If the new value of the event is not convertible to {@code <E>}, a ClassCastException with a detailed message will be thrown. The function will not be
     * called and the value of the property will not be set.
     * <p>
     * The property will be set to the (nullable) return value of the method.
     * <p>
     * Note that the conversion of the new value of the event to the required type of {@code transformer} may lead to a {@link ClassCastException} being thrown
     * when the event listener is invoked. The caller should be careful to only bind this method to properties that can only have convertible new values.
     *
     * @param vocab        The StandardVocab to bind the PropertyChangeListener to.
     * @param property     The property to write to on events.
     * @param propertyName The property name to listen on.
     * @param transformer  A function that takes the new value as a parameter and transforms it into a value that the property is to be set to.
     * @param <P>          The type of the value of the property.
     * @param <E>          The type of the new values the listened property can be set to.
     * @return The PropertyChangeListener that was bound to the StandardVocab.
     * @since 0.2.0
     */
    @SuppressWarnings("unchecked")
    public static <P, E> PropertyChangeListener bindTo(@NotNull StandardVocab vocab, @NotNull WritableValue<P> property, @NotNull @NonNls
    String propertyName, @NotNull Function<E, P> transformer)
    {
        checkNotNull(vocab);
        checkNotNull(property);
        checkNotEmpty(propertyName);
        checkNotNull(transformer);

        @NotNull PropertyChangeListener listener = evt -> {
            Object newValue = evt.getNewValue();
            try
            {
                P returned = transformer.apply((E) newValue);
                property.setValue(returned);
            } catch (ClassCastException e)
            {
                throw (ClassCastException) new ClassCastException("Listened property " + property +
                                                                  " on vocab " + vocab +
                                                                  " got inconvertible new value " + newValue +
                                                                  " of type " + newValue.getClass().getName() +
                                                                  " (could not write to property " + property + ")").initCause(e);

            }
        };
        vocab.addPropertyChangeListener(propertyName, listener);
        return listener;
    }
}
