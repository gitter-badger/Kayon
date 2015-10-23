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

package cf.kayon.cli.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The application class for the JavaFX command line interface.
 *
 * @author Ruben Anders
 * @see FxController
 * @since 0.0.1
 * @deprecated Use the graphical JavaFX interface instead.
 */
@Deprecated
public final class FxInterface extends Application
{

    /**
     * Launches the JavaFX command line interface.
     *
     * @param args The program arguments.
     * @since 0.0.1
     */
    @Deprecated
    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.0.1
     */
    @Deprecated
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(this.getClass().getResource("/cf/kayon/cli/fx/FxInterface.fxml"));
        Scene scene = new Scene(root, 1200, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
