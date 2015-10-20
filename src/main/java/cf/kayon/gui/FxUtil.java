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

import com.google.common.collect.Sets;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableValue;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FxUtil
{
    private static final Set<Image> images = Sets.newHashSet(new Image(FxUtil.class.getResourceAsStream("/cf/kayon/gui/logo16.png")),
                                                             new Image(FxUtil.class.getResourceAsStream("/cf/kayon/gui/logo32.png")),
                                                             new Image(FxUtil.class.getResourceAsStream("/cf/kayon/gui/logo64.png")),
                                                             new Image(FxUtil.class.getResourceAsStream("/cf/kayon/gui/logo128.png")),
                                                             new Image(FxUtil.class.getResourceAsStream("/cf/kayon/gui/logo150.png")));

    public static void initIcons(Stage stage)
    {
        stage.getIcons().addAll(images);
    }

    public static void sleepSafely(int seconds)
    {
        try
        {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ignored) {}
    }

    public static ChangeListener<Boolean> bindInverse(WritableBooleanValue writeTo, ReadOnlyBooleanProperty readFrom)
    {
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> Platform.runLater(() -> writeTo.set(!newValue));
        readFrom.addListener(listener);
        return listener;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> PropertyChangeListener bind(WritableValue<T> writeTo, String propertyName)
    {
        return evt -> {
            if (evt.getPropertyName().equals(propertyName))
                writeTo.setValue((T) evt.getNewValue());
        };
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T, R> PropertyChangeListener bind(WritableValue<R> writeTo, String propertyName, Function<T, R> transformer)
    {
        return evt -> {
            if (evt.getPropertyName().equals(propertyName))
                writeTo.setValue(transformer.apply((T) evt.getNewValue()));
        };
    }
}
