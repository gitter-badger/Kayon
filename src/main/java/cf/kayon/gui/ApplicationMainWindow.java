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

import cf.kayon.core.util.KayonReference;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public final class ApplicationMainWindow extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("cf.kayon.gui.bundles.ApplicationMainWindow"));
        loader.setLocation(getClass().getResource("/cf/kayon/gui/ApplicationMainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.getIcons().addAll(new Image(getClass().getResource("/cf/kayon/gui/logo16.png").toExternalForm()),
                                       new Image(getClass().getResource("/cf/kayon/gui/logo32.png").toExternalForm()),
                                       new Image(getClass().getResource("/cf/kayon/gui/logo64.png").toExternalForm()),
                                       new Image(getClass().getResource("/cf/kayon/gui/logo128.png").toExternalForm()),
                                       new Image(getClass().getResource("/cf/kayon/gui/logo150.png").toExternalForm()));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Kayon " + KayonReference.getVersion() + " (b" + KayonReference.getBuild() + ")");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
