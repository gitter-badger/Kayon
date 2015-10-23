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

import cf.kayon.cli.CommandLineAccessor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Controls the JavaFX command line interface.
 *
 * @author Ruben Anders
 * @see FxInterface
 * @since 0.0.1
 * @deprecated Use the graphical JavaFX interface instead.
 */
@Deprecated
public class FxController
{
    /**
     * The command line accessor instance.
     *
     * @since 0.0.1
     */
    @Deprecated
    private final CommandLineAccessor cla = new CommandLineAccessor();

    /**
     * The results area.
     *
     * @since 0.0.1
     */
    @Deprecated
    @FXML
    private TextArea resultsArea;

    /**
     * The execute button.
     *
     * @since 0.0.1
     */
    @Deprecated
    @FXML
    private Button button;

    /**
     * The text field for command input.
     *
     * @since 0.0.1
     */
    @Deprecated
    @FXML
    private TextField textField;

    /**
     * Makes sure a string ends with a newline character.
     * <p>
     * If the string already contains a newline as its last character, the original string is returned.
     * Otherwise, the old String with an concatenated newline is returned.
     *
     * @param oldStr A string to ensure the newline on.
     * @return A string that definitely ends in a newline.
     * @since 0.0.1
     */
    @Deprecated
    @NotNull
    private static String ensureNewLine(@NotNull String oldStr)
    {
        checkNotNull(oldStr);
        if (oldStr.length() >= 1 && oldStr.charAt(oldStr.length() - 1) != '\n')
        {
            return oldStr + '\n';
        }
        return oldStr;
    }

    /**
     * The event handler function for a button click as defined in the FXML file.
     *
     * @param event The ActionEvent for the button click.
     * @since 0.0.1
     */
    @Deprecated
    @FXML
    protected void executeButtonClick(ActionEvent event)
    {
        String command = textField.getText();
        if (command != null)
        {
            textField.clear();
            String oldText = resultsArea.getText();
            String newText = ensureNewLine(oldText) + "FXController@Kayon $ " + command + '\n';
            resultsArea.setText(newText);

            Pair<String, Boolean> pair = cla.processCommand(command);
            if (pair.getRight())
                resultsArea.setText(newText + pair.getLeft());
            else
                ((Stage) resultsArea.getScene().getWindow()).close();
        }
    }

    /**
     * Initializes the controller.
     *
     * @see javafx.fxml.Initializable
     * @since 0.0.1
     */
    @Deprecated
    public void initialize()
    {
        //        resultsArea.textProperty().addListener((observable, oldStr, newStr) -> {
        //            resultsArea.setScrollTop(Double.MAX_VALUE);
        //        });

        Platform.runLater(textField::requestFocus);

        Platform.runLater(() -> resultsArea.setText(cla.processCommand("help").getLeft()));
    }
}
