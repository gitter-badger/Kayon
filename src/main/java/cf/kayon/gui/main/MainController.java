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
    GridPane mainPane;

    /**
     * The search term text box.
     *
     * @since 0.0.1
     */
    @FXML
    TextField searchField;

    /**
     * The "Search" button.
     *
     * @since 0.0.1
     */
    @FXML
    Button searchButton;

    /**
     * The VBox contained in the ScrollPane.
     *
     * @since 0.0.1
     */
    @FXML
    VBox vBox;

    /**
     * The progress indicator (initially hidden under the search button).
     *
     * @since 0.0.1
     */
    @FXML
    ProgressIndicator progressIndicator;

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
        progressIndicator.setVisible(true);
        searchButton.setVisible(false);
        searchField.setDisable(true);
        queryVocab(searchField.getText());
    }

    /**
     * Initializes this MainController.
     *
     * @see javafx.fxml.Initializable
     * @since 0.0.1
     */
    public void initialize()
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchButton.setDisable(newValue == null || newValue.isEmpty()));
    }

    /**
     * Queries vocab from the database. Triggered by an enter press or a button click.
     * <p>
     * Handles both uppercase and lowercase characters.
     *
     * @param searchString The search string (user input).
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    private void queryVocab(@NotNull String searchString)
    {
        LOGGER.info("Querying for user input >" + searchString + "<");

        final ArrayBlockingQueue<Vocab> queue = new ArrayBlockingQueue<>(FxUtil.context.getConfig().getInt("gui.main.queryQueueSize"));
        final Vocab poison = StandardVocab.newPoison();
        // delegates toLowerCase() and regex escaping
        final QueryTask producer = new QueryTask(FxUtil.context, searchString, queue, poison);

        int iMax = FxUtil.context.getConfig().getInt("gui.main.reconstructThreads");
        final CountDownLatch latch = new CountDownLatch(iMax);
        for (int i = 0; i < iMax; i++)
        {
            final NodeTask consumer = new NodeTask(vBox, queue, poison, latch);
            FxUtil.executor.submit(consumer);
        }
        FxUtil.executor.submit(producer);

        final ReEnabler reEnabler = new ReEnabler(latch, this);
        FxUtil.executor.execute(reEnabler);
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
            progressIndicator.setVisible(false);
            searchButton.setVisible(true);
            searchField.setDisable(false);
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
     * @since 0.2.3
     */
    @FXML
    private void generateNouns(ActionEvent event)
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
