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

package cf.kayon.gui.main;

import cf.kayon.gui.FxUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Entry point for creating the main application view.
 *
 * @author Ruben Anders
 * @see MainController
 * @since 0.0.1
 */
public class Main
{

    /**
     * Creates a new Parent element from the FXML file.
     * <p>
     * Note that the FXML's file contents are not buffered and will be loaded from the classpath every time this method is called.
     *
     * @return A Pair: The new parent and its controller class instance.
     * @throws IOException If a I/O exception occurs when loading the FXML/resource bundle files.
     * @see #createNewScene()
     * @see #createOntoStage(Stage)
     * @since 0.0.1
     */
    @NotNull
    public static Pair<Parent, MainController> createNewParent() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("cf.kayon.gui.main.main"));
        loader.setLocation(Main.class.getResource("/cf/kayon/gui/main/main.fxml"));
        Parent parent = loader.load();
        MainController controller = loader.getController();
        return new ImmutablePair<>(parent, controller);
    }

    /**
     * Creates a new Scene with the main parent on it.
     * <p>
     * The Parent of the scene is a new parent as returned by {@link #createNewParent()}.
     *
     * @return A Pair: The new scene and its controller class instance.
     * @throws IOException If an I/O exception occurs when loading the FXML/resource bundle files.
     * @see #createNewParent()
     * @see #createOntoStage(Stage)
     * @since 0.0.1
     */
    @NotNull
    public static Pair<Scene, MainController> createNewScene() throws IOException
    {
        Pair<Parent, MainController> pair = createNewParent();
        return new ImmutablePair<>(new Scene(pair.getLeft()), pair.getRight());
    }

    /**
     * Initializes a Stage with the main view scene.
     * <p>
     * The stage's icons are set, its title is set and its scene is set to a new scene as returned by {@link #createNewScene()}.
     * <p>
     * This method should only be called on the JavaFX application thread.
     *
     * @param stage The stage to initialize.
     * @return The controller class instance.
     * @throws IOException          If an I/O exception occurs when loading the FXML/resource bundle files.
     * @throws NullPointerException If {@code stage} is {@code null}.
     * @see #createNewParent()
     * @see #createNewScene()
     * @since 0.0.1
     */
    @NotNull
    public static MainController createOntoStage(@NotNull Stage stage) throws IOException
    {
        checkNotNull(stage);
        FxUtil.initIcons(stage);
        Pair<Scene, MainController> pair = createNewScene();
        stage.setTitle("Kayon " + FxUtil.context.getVersion() + " (b" + FxUtil.context.getBuild() + ")");
        stage.setScene(pair.getLeft());
        return pair.getRight();
    }
}
