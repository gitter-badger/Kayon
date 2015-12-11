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
import com.google.common.collect.Lists;
import javafx.beans.value.WritableValue;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
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
public class FxUtil
{
    /**
     * A set containing the images for the application.
     *
     * @since 0.2.0
     */
    @SuppressWarnings("HardcodedFileSeparator")
    private static final List<Image> images =
            Collections.unmodifiableList(Lists.newArrayList(new Image(FxUtil.class.getResource("/cf/kayon/gui/logo16.png").toExternalForm(), true),
                                                            new Image(FxUtil.class.getResource("/cf/kayon/gui/logo32.png").toExternalForm(), true),
                                                            new Image(FxUtil.class.getResource("/cf/kayon/gui/logo64.png").toExternalForm(), true)));
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
     * Adds the default icons (16px, 32px and 64px) to the specified stage.
     *
     * @param stage The stage to add the icons to.
     * @since 0.2.0
     */
    public static void initIcons(@NotNull Stage stage)
    {
        checkNotNull(stage);
        stage.getIcons().addAll(images);
    }

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
