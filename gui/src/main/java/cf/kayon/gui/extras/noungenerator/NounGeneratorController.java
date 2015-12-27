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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

@NotThreadSafe
public class NounGeneratorController
{

    @FXML
    ResourceBundle resources;
    @FXML
    private ToggleGroup characterSet;
    @FXML
    private RadioButton alphanumericRadioButton;
    @FXML
    private Spinner<Integer> countSpinner, lengthSpinner;
    @FXML
    private GridPane root;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextField seedTextField;
    @Nullable
    private GeneratorTask currentTask;

    /**
     * @see javafx.fxml.Initializable
     * @since 0.2.3
     */
    @FXML
    private void initialize()
    {
        countSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1, 1));
        lengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 8, 1));

        root.sceneProperty().addListener((observable, oldValue, newScene) -> {
            if (newScene != null)
                newScene.windowProperty().addListener((observable1, oldValue1, newWindow) -> {
                    if (newWindow != null)
                        newWindow.setOnCloseRequest(event -> {
                            if (currentTask != null)
                                currentTask.cancel();
                        });
                });

        });
    }

    private void disableUI()
    {
        setDisableState(root, true);
    }

    private void enableUI()
    {
        setDisableState(root, false);
    }

    private void setDisableState(Pane pane, boolean flag)
    {
        pane.getChildren().stream().forEach(node -> {
            if (node.getStyle().equals("-x-kayon-disable: true;"))
                node.setDisable(flag);
            else if (node.getStyle().equals("-x-kayon-disable: false;"))
            {
                node.setDisable(!flag);
            }
            if (node instanceof Pane)
                setDisableState((Pane) node, flag); // recursive call
        });
    }

    @FXML
    private void run(ActionEvent event)
    {
        currentTask = new GeneratorTask(
                FxUtil.context,
                characterSet.getSelectedToggle() == alphanumericRadioButton,
                lengthSpinner.getValue(),
                countSpinner.getValue(),
                seedTextField.getText());

        progressBar.progressProperty().bind(currentTask.progressProperty());

        currentTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue)
            {
                case CANCELLED: // do not hide window
                    progressBar.progressProperty().unbind();
                    progressBar.progressProperty().set(0d);
                    enableUI();
                    break;
                case SUCCEEDED:
                case FAILED:
                    root.getScene().getWindow().hide();
                    break;
                case READY:
                case RUNNING:
                case SCHEDULED:
                    disableUI();
                    break;
            }
        });

        FxUtil.executor.execute(currentTask);
    }

    @FXML
    private void cancel(ActionEvent event)
    {
        if (currentTask != null)
            currentTask.cancel(false);
    }
}
