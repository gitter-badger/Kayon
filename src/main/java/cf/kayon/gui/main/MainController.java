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

import cf.kayon.core.CaseHandling;
import cf.kayon.core.StandardVocab;
import cf.kayon.core.Vocab;
import cf.kayon.gui.FxUtil;
import cf.kayon.gui.extras.noungenerator.NounGenerator;
import cf.kayon.gui.vocabview.nounview.NounView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Controls the main view.
 *
 * @author Ruben Anders
 * @see Main
 * @since 0.0.1
 */
@NotThreadSafe
public class MainController
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    /**
     * The main grid pane.
     *
     * @since 0.0.1
     */
    @FXML
    private GridPane mainPane;

    /**
     * The search term text box.
     *
     * @since 0.0.1
     */
    @FXML
    private TextField searchField;

    /**
     * The "Search" button.
     *
     * @since 0.0.1
     */
    @FXML
    private Button searchButton;

    /**
     * The "Search by root word" button.
     *
     * @since 0.2.3
     */
    @FXML
    private Button rootSearchButton;

    /**
     * The VBox contained in the ScrollPane.
     *
     * @since 0.0.1
     */
    @FXML
    private VBox vBox;

    /**
     * The progress indicator (initially hidden under the search button).
     *
     * @since 0.0.1
     */
    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * Handles a search button press. Bound to the button in the FXML file.
     *
     * @param e The event.
     * @since 0.0.1
     */
    @FXML
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    private void search(ActionEvent e)
    {
        LOGGER.info("Search request received");
        queryVocab(searchField.getText(), false);
    }

    private void setSearchActive(boolean isSearchActive)
    {
        progressIndicator.setVisible(isSearchActive);
        searchButton.setVisible(!isSearchActive);
        rootSearchButton.setVisible(!isSearchActive);
        searchField.setDisable(isSearchActive);
    }

    @FXML
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    private void rootSearch(ActionEvent e)
    {
        LOGGER.info("Root word search request received");
        queryVocab(searchField.getText(), true);
    }

    /**
     * Initializes this MainController.
     *
     * @see javafx.fxml.Initializable
     * @since 0.0.1
     */
    public void initialize()
    {
        searchButton.disableProperty().bind(searchField.textProperty().isEmpty());
        rootSearchButton.disableProperty().bind(searchField.textProperty().isEmpty());
    }

    /**
     * Queries vocab from the database. Triggered by an enter press or a button click.
     * <p>
     * Handles both uppercase and lowercase characters.
     *
     * @param searchString The search string (user input).
     * @param byRootWord   {@code false} if {@code searchString} is a finite form to search for, {@code true} if {@code searchString} is a root word to search for
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    private void queryVocab(@NotNull String searchString, boolean byRootWord)
    {
        LOGGER.info("Querying for user input >" + searchString + "<");
        setSearchActive(true);

        final ArrayBlockingQueue<Vocab> queue = new ArrayBlockingQueue<>(FxUtil.context.getConfig().getInt("gui.main.queryQueueSize"));
        final Vocab poison = StandardVocab.newPoison();
        // delegates toLowerCase() and regex escaping
        final QueryTask producer = new QueryTask(FxUtil.context, searchString, queue, poison, byRootWord);

        int iMax = FxUtil.context.getConfig().getInt("gui.main.reconstructThreads");
        final CountDownLatch latch = new CountDownLatch(iMax);

        FxUtil.executor.submit(producer);
        final ReEnabler reEnabler = new ReEnabler(latch, this);
        FxUtil.executor.execute(reEnabler);

        for (int i = 0; i < iMax; i++)
        {
            final NodeTask consumer = new NodeTask(vBox, queue, poison, latch);
            FxUtil.executor.submit(consumer);
        }
    }

    /**
     * Resets the UI back to non-in-query mode.
     *
     * @since 0.0.1
     */
    /* package-local */
    void resetUI()
    {
        Platform.runLater(() -> {
            LOGGER.info("Resetting UI back to normal");
            setSearchActive(false);
            searchField.requestFocus();
            searchField.clear(); // Also disables button
        });
    }

    /**
     * Called when the user selects the "New Noun" entry from the manage menu.
     * Bound in the FXML file.
     *
     * @param event The event.
     * @since 0.0.1
     */
    @FXML
    private void newNoun(ActionEvent event)
    {
        LOGGER.info("Launching dialog for new noun");
        Stage newStage = new Stage();
        try
        {
            NounView.createOntoStage(newStage, null);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        newStage.initOwner(mainPane.getScene().getWindow());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();
    }

    /**
     * Called when the user selects the "Generate nouns..." option in the extras menu.
     *
     * @param event The ActionEvent passed in by JavaFX (will be discarded)
     * @since 0.2.3
     */
    @FXML
    private void generateNouns(@Nullable ActionEvent event)
    {
        LOGGER.info("Launching dialog for generating nouns");
        Stage newStage = new Stage();
        try
        {
            NounGenerator.createOntoStage(newStage);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        newStage.initOwner(mainPane.getScene().getWindow());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();
    }
}
