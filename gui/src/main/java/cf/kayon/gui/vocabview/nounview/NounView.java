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

package cf.kayon.gui.vocabview.nounview;

import cf.kayon.core.noun.Noun;
import cf.kayon.gui.FxUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The JavaFX noun view.
 *
 * @author Ruben Anders
 * @see NounViewController
 * @since 0.0.1
 */
public class NounView
{

    /**
     * The FXML file's bytes, buffered in a byte array.
     *
     * @since 0.0.1
     */
    @NotNull
    private static final byte[] FXML;

    static
    {
        //noinspection HardcodedFileSeparator
        try (InputStream inputStream = NounView.class.getResourceAsStream("/cf/kayon/gui/vocabview/nounview/nounview.fxml"))
        {
            FXML = IOUtils.toByteArray(inputStream);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new Parent element from the FXML file.
     * <p>
     * Note that the FXML's file contents are buffered and will be loaded from the classpath only once (on static class initialization, or, more precisely, the first
     * request to construct a NounView).
     * <p>
     * It's safe to call this method
     * <ol>
     * <li>
     * on any thread
     * </li>
     * <li>concurrently</li>
     * </ol>
     *
     * @param noun The noun to initialize the NounView with.
     * @return A Pair: The new parent and its controller class instance.
     * @throws IOException If a I/O exception occurs when loading the FXML/resource bundle files.
     * @see #createNewScene(Noun)
     * @see #createOntoStage(Stage, Noun)
     * @since 0.0.1
     */
    @NotNull
    public static Pair<Parent, NounViewController> createNewParent(@Nullable Noun noun) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("cf.kayon.gui.vocabview.nounview.nounview"));
        //noinspection HardcodedFileSeparator
        loader.setLocation(NounView.class.getResource("/cf/kayon/gui/vocabview/noun/nounview.fxml"));
        Parent parent = loader.load(new ByteArrayInputStream(FXML));
        NounViewController controller = loader.getController();
        controller.bindNoun(noun, true, true);
        return new ImmutablePair<>(parent, controller);
    }

    /**
     * Creates a new Scene with the noun view parent on it.
     * <p>
     * The Parent of the scene is a new parent as returned by {@link #createNewParent(Noun)}.
     *
     * @param noun The noun to initialize the NounView with.
     * @return A Pair: The new scene and its controller class instance.
     * @throws IOException If an I/O exception occurs when loading the FXML/resource bundle files.
     * @see #createNewParent(Noun)
     * @see #createOntoStage(Stage, Noun)
     * @since 0.0.1
     */
    @NotNull
    public static Pair<Scene, NounViewController> createNewScene(@Nullable Noun noun) throws IOException
    {
        Pair<Parent, NounViewController> pair = createNewParent(noun);
        return new ImmutablePair<>(new Scene(pair.getLeft()), pair.getRight());
    }

    /**
     * Initializes a Stage with the noun view view scene.
     * <p>
     * The stage's icons are set, its title is set and its scene is set to a new scene as returned by {@link #createNewScene(Noun)}.
     * <p>
     * This method should only be called on the JavaFX application thread.
     *
     * @param stage The stage to initialize.
     * @param noun  The noun to initialize the NounView with.
     * @return The controller class instance.
     * @throws IOException          If an I/O exception occurs when loading the FXML/resource bundle files.
     * @throws NullPointerException If {@code stage} is {@code null}.
     * @see #createNewParent(Noun)
     * @see #createNewScene(Noun)
     * @since 0.0.1
     */
    @NotNull
    public static NounViewController createOntoStage(@NotNull Stage stage, @Nullable Noun noun) throws IOException
    {
        checkNotNull(stage);
        FxUtil.initLogo(stage);
        stage.setResizable(false);

        Pair<Scene, NounViewController> pair = createNewScene(noun);
        stage.setTitle(pair.getRight().resources.getString("WindowTitle"));
        stage.setScene(pair.getLeft());
        return pair.getRight();
    }
}
