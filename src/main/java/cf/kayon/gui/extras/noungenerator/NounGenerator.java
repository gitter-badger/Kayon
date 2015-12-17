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

package cf.kayon.gui.extras.noungenerator;

import cf.kayon.gui.FxUtil;
import cf.kayon.gui.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Entry point for creating a NounGenerator view.
 *
 * @author Ruben Anders
 * @see NounGeneratorController
 * @since 0.2.3
 */
@Immutable
public class NounGenerator
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
     * @since 0.2.3
     */
    @NotNull
    public static Pair<Parent, NounGeneratorController> createNewParent() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("cf.kayon.gui.extras.noungenerator.noungenerator"));
        //noinspection HardcodedFileSeparator
        loader.setLocation(Main.class.getResource("/cf/kayon/gui/extras/noungenerator/noungenerator.fxml"));
        Parent parent = loader.load();
        NounGeneratorController controller = loader.getController();
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
     * @since 0.2.3
     */
    @NotNull
    public static Pair<Scene, NounGeneratorController> createNewScene() throws IOException
    {
        Pair<Parent, NounGeneratorController> pair = createNewParent();
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
     * @since 0.2.3
     */
    @NotNull
    public static NounGeneratorController createOntoStage(@NotNull Stage stage) throws IOException
    {
        checkNotNull(stage);
        FxUtil.initIcons(stage);
        Pair<Scene, NounGeneratorController> pair = createNewScene();
        stage.setScene(pair.getLeft());
        stage.setTitle(pair.getRight().resources.getString("WindowTitle"));
        return pair.getRight();
    }
}
