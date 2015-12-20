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

package cf.kayon.gui.splash;

import cf.kayon.core.KayonContext;
import cf.kayon.gui.FxUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The JavaFX splash screen.
 *
 * @author Ruben Anders
 * @see SplashController
 * @since 0.0.1
 */
public class Splash extends Application
{
    /**
     * The main entry point for the application.
     *
     * @param args The arguments the program was launched with.
     * @since 0.0.1
     */
    public static void main(@Nullable String[] args)
    {
        System.setProperty("glass.accessible.force", "false"); // http://stackoverflow.com/a/32597281/4464702 (Windows 10 devices with touch)
        // Fixed in Java 9 only
        launch(args);
    }

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
    public static Pair<Parent, SplashController> createNewParent() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("cf.kayon.gui.splash.splash"));
        //noinspection HardcodedFileSeparator
        loader.setLocation(Splash.class.getResource("/cf/kayon/gui/splash/splash.fxml"));
        Parent parent = loader.load();
        SplashController controller = loader.getController();
        return new ImmutablePair<>(parent, controller);
    }

    /**
     * Creates a new Scene with the splash screen parent on it.
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
    public static Pair<Scene, SplashController> createNewScene() throws IOException
    {
        Pair<Parent, SplashController> pair = createNewParent();
        return new ImmutablePair<>(new Scene(pair.getLeft()), pair.getRight());
    }

    /**
     * Initializes a Stage with the splash screen scene.
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
    public static SplashController createOntoStage(@NotNull Stage stage) throws IOException
    {
        checkNotNull(stage);
        FxUtil.initLogo(stage); // For taskbar
        stage.initStyle(StageStyle.UNDECORATED);

        Pair<Scene, SplashController> pair = createNewScene();
        stage.setScene(pair.getLeft());

        return pair.getRight();
    }

    /**
     * @throws Exception If an {@link java.sql.SQLException} occurs when closing the connection of the application KayonContext ({@link FxUtil#context})
     * @since 0.2.0
     */
    @Override
    public void stop() throws Exception
    {
        super.stop();
        @Nullable
        KayonContext context = FxUtil.context;
        if (context != null) // If object is present, its contents are also present (null checks are performed on construct)
        {
            context.getConnection().close();
        }
        @Nullable
        ThreadPoolExecutor executor = FxUtil.executor;
        if (executor != null)
        {
            executor.shutdown(); // Complete all tasks and terminate pool threads
        }
    }

    /**
     * @since 0.0.1
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        SplashController controller = createOntoStage(stage);
        stage.show();
        controller.startApplication();
    }
}
