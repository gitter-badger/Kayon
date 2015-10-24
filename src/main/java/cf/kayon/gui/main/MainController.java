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
import cf.kayon.core.Vocab;
import cf.kayon.core.sql.ConnectionHolder;
import cf.kayon.gui.vocabview.noun.NounView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Controls the main view.
 *
 * @author Ruben Anders
 * @see Main
 * @since 0.0.1
 */
public class MainController
{

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
     * The progress indicator hidden under the search button.
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
    private void handleButtonPress(ActionEvent e)
    {
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
     * Queries vocab from the database. Triggered by a enter press or a button click.
     * <p>
     * Handles both uppercase and lowercase characters.
     *
     * @param searchString The search string.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    private void queryVocab(@NotNull String searchString)
    {
        VocabTask vocabTask = new VocabTask(searchString, ConnectionHolder.getConnection());
        vocabTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue)
            {
                case SUCCEEDED:
                    constructNodes(vocabTask.getValue());
                    break;
                case CANCELLED:
                case FAILED:
                    resetUI();
                    break;
                case READY:
                case RUNNING:
                case SCHEDULED:
                    vBox.getChildren().clear();
                    break;
            }
        });
        new Thread(vocabTask).start();
    }

    /**
     * Constructs the view nodes (and appends them to the vBox) from a list of queried vocab.
     *
     * @param serviceResult The result of the vocab database query.
     * @since 0.0.1
     */
    private void constructNodes(List<Vocab> serviceResult)
    {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 16, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch latch = new CountDownLatch(serviceResult.size());
        for (Vocab vocab : serviceResult)
        {
            NodeTask task = new NodeTask(vocab, vBox, latch);
            threadPoolExecutor.execute(task);
        }
        threadPoolExecutor.shutdown();
        new Thread(() -> {
            try
            {
                latch.await();
            } catch (InterruptedException ignored) {}
            resetUI();
        }).start();
    }

    /**
     * Resets the UI back to non-in-query mode.
     *
     * @since 0.0.1
     */
    private void resetUI()
    {
        Platform.runLater(() -> {
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
        Stage newStage = new Stage();
        try
        {
            NounView.createOntoStage(newStage, null);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        newStage.show();
    }
}
