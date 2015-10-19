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

import cf.kayon.core.Vocab;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.sql.StaticConnectionHolder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApplicationMainWindowController
{

    @FXML
    GridPane mainPane;

    @FXML
    Menu menuManage;

    @FXML
    TextField searchField;

    @FXML
    Button searchButton;

    @FXML
    FlowPane flowPane;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    private void handleButtonPress(ActionEvent e)
    {
        String searchString = searchField.getText(); // null and empty checking is done by disabling button
        progressIndicator.setVisible(true);
        searchButton.setVisible(false);
        searchField.setDisable(true);
        VocabTask vocabTask = new VocabTask(searchString, StaticConnectionHolder.connectionForId("main"));
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
                    flowPane.getChildren().clear();
                    break;
            }
        });
        new Thread(vocabTask).start();
    }

    /**
     * @see javafx.fxml.Initializable
     */
    public void initialize()
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchButton.setDisable(newValue == null || newValue.isEmpty()));
    }

    private void constructNodes(Set<Vocab> serviceResult)
    {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 16, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch latch = new CountDownLatch(serviceResult.size());
        for (Vocab vocab : serviceResult)
        {
            NodeTask task = new NodeTask(vocab, flowPane, latch);
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

    @FXML
    private void newNoun(ActionEvent event)
    {
        Stage newStage = new Stage();
        newStage.setResizable(false);
        ResourceBundle bundle = ResourceBundle.getBundle("cf.kayon.gui.bundles.VocabView");
        newStage.setTitle(bundle.getString("Noun.WindowTitle"));
        FxUtil.initIcons(newStage);
        newStage.initOwner(mainPane.getScene().getWindow());
        newStage.initModality(Modality.WINDOW_MODAL);
        Node node = VocabNodeFactory.forType(Noun.class);
        checkNotNull(node); // Throw NullPointerException (superclass of RuntimeException) if returned node is null
        Scene newScene = new Scene((Parent) node);
        newStage.setScene(newScene);
        newStage.show();
    }

    @FXML
    private void newAdjective(ActionEvent event)
    {
        //TODO
    }

    @FXML
    private void newVerb(ActionEvent event)
    {
        //TODO
    }

    @FXML
    private void newAdverb(ActionEvent event)
    {
        //TODO
    }

    @FXML
    private void newPreposition(ActionEvent event)
    {
        //TODO
    }
}
