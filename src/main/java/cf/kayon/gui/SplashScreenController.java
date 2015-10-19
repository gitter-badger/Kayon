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

import cf.kayon.core.sql.NounSQLFactory;
import cf.kayon.core.sql.StaticConnectionHolder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SplashScreenController
{
    @FXML
    ResourceBundle resources;

    @FXML
    ProgressBar progress;

    @FXML
    Pane pane;

    @FXML
    Text text;

    private void connectToDatabase()
    {
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            StaticConnectionHolder.registerNewConnection("main", connection);
            //            throw new SQLException();
        } catch (SQLException e)
        {
            splashException("ConnectionFailure", e);
        }
    }

    private void splashException(String resourceBundlePackage, Throwable t)
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format(resources.getString("Error." + resourceBundlePackage + ".Title"),
                                         t.getClass().toString(), t.getClass().getName(), orNoMessage(t.getMessage()), orNoMessage(t.getLocalizedMessage())));
            alert.setHeaderText(String.format(resources.getString("Error." + resourceBundlePackage + ".HeaderText"),
                                              t.getClass().toString(), t.getClass().getName(), orNoMessage(t.getMessage()), orNoMessage(t.getLocalizedMessage())));
            alert.setContentText(String.format(resources.getString("Error." + resourceBundlePackage + ".ContentText"),
                                               t.getClass().toString(), t.getClass().getName(), orNoMessage(t.getMessage()), orNoMessage(t.getLocalizedMessage())));
            ((Stage) pane.getScene().getWindow()).setAlwaysOnTop(false);
            FxUtil.initIcons((Stage) alert.getDialogPane().getScene().getWindow());
            alert.initOwner(pane.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
            Platform.exit();
            new Thread(() -> { // Let JavaFX
                FxUtil.sleepSafely(1);
                System.exit(1);
            }, "DelayedSystemExit").start();
        });
        try
        {
            this.wait();
        } catch (InterruptedException ignored) {}
    }

    @NotNull
    private String orNoMessage(@Nullable String nullableString)
    {
        if (nullableString != null)
            return nullableString;
        return resources.getString("Error.NoMessageAlternative");
    }

    private void initializeDatabaseStructure()
    {
        Connection connection = StaticConnectionHolder.connectionForId("main");
        if (connection == null)
            throw new Error("No Database 'main'!");
        try
        {
            NounSQLFactory.setupDatabaseForNouns(connection);
            //            throw new SQLException();
        } catch (SQLException e)
        {
            splashException("StructureSetupFailure", e);
        }
    }

    public void startApplication()
    {
        Task<Void> applicationStartTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                updateProgress(0, 0);
                Platform.runLater(() -> updateMessage(resources.getString("ConnectingToDatabase")));

                connectToDatabase();
                //                FxUtil.sleepSafely(1);
                Platform.runLater(() -> {
                    updateProgress(1, 2);
                    updateMessage(resources.getString("InitializingDatabaseStructure"));
                });

                initializeDatabaseStructure();
                //                FxUtil.sleepSafely(1);


                Platform.runLater(() -> {
                    updateProgress(2, 2);
                    updateMessage(resources.getString("OpeningMainWindow"));
                    openMainWindow();
                });
                //                FxUtil.sleepSafely(1);
                Platform.runLater(pane.getScene().getWindow()::hide);

                return null;
            }
        };
        progress.progressProperty().bind(applicationStartTask.progressProperty());
        text.textProperty().bind(applicationStartTask.messageProperty());

        Thread th = new Thread(applicationStartTask, "Application Start Task");
        th.start();
    }

    private void openMainWindow()
    {
        ApplicationMainWindow applicationMainWindow = new ApplicationMainWindow();
        Stage stage = new Stage();
        try
        {
            applicationMainWindow.start(stage);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

