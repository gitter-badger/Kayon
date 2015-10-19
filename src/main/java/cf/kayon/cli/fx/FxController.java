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

public class FxController
{
    private final CommandLineAccessor cla = new CommandLineAccessor();

    @FXML
    TextArea resultsArea;

    @FXML
    Button button;

    @FXML
    TextField textField;

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

    @FXML
    protected void executeButtonClick(ActionEvent event)
    {
        String command = textField.getText();
        if (command != null)
        {
            textField.setText("");
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

    public void initialize()
    {
        //        resultsArea.textProperty().addListener((observable, oldStr, newStr) -> {
        //            resultsArea.setScrollTop(Double.MAX_VALUE);
        //        });

        Platform.runLater(textField::requestFocus);

        Platform.runLater(() -> resultsArea.setText(cla.processCommand("help").getLeft()));
    }
}
