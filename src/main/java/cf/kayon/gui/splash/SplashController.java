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

import cf.kayon.core.sql.ConnectionHolder;
import cf.kayon.core.sql.NounSQLFactory;
import cf.kayon.gui.FxUtil;
import cf.kayon.gui.main.Main;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Controller for the splash screen.
 */
public class SplashController
{
    @FXML
    ResourceBundle resources;

    @FXML
    ProgressBar progress;

    @FXML
    Pane pane;

    @FXML
    Text text;

    /**
     * Makes the application-wide connection to the H2 Database.
     *
     * @since 0.0.1
     */
    private void connectToDatabase()
    {
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:h2:./database");
            ConnectionHolder.initializeConnection(connection);
        } catch (Throwable t)
        {
            splashException("ConnectionFailure", t, 1);
        }
    }

    /**
     * Shows an exception alert.
     * <p>
     * NOT to be called on the JavaFX application thread, since the current thread will be halted until exit.
     *
     * @param resourceBundlePackage The resource bundle package name of the error.
     * @param t                     The throwable to show in the alert.
     * @param exitStatus            The exit status to exit the application with.
     * @since 0.0.1
     */
    private void splashException(String resourceBundlePackage, Throwable t, int exitStatus)
    {
        t.printStackTrace(); // For advanced debugging, the splash does not show the stack, etc...
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            String throwableClassFullName = t.getClass().getCanonicalName();
            String unLocalizedMessage = orNoMessage(t.getMessage());

            alert.setTitle(MessageFormat.format(resources.getString("Error." + resourceBundlePackage + ".Title"),
                                                throwableClassFullName, unLocalizedMessage));
            alert.setHeaderText(MessageFormat.format(resources.getString("Error." + resourceBundlePackage + ".HeaderText"),
                                                     throwableClassFullName, unLocalizedMessage));
            alert.setContentText(MessageFormat.format(resources.getString("Error." + resourceBundlePackage + ".ContentText"),
                                                      throwableClassFullName, unLocalizedMessage));

            Stage stage = (Stage) pane.getScene().getWindow();
            stage.setAlwaysOnTop(false);
            FxUtil.initIcons(stage);

            alert.initOwner(pane.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
            Platform.exit();
            System.exit(exitStatus);
        });
        try
        {
            this.wait(); // Halt the task thread execution
        } catch (InterruptedException ignored) {}
    }

    /**
     * Utility method to get a translated alternating string if the string parameter is {@code null}.
     *
     * @param nullableString The string that may be null.
     * @return The string passed in if it is not {@code null}, or a translated alternative if it was {@code null}.
     * @since 0.0.1
     */
    @NotNull
    private String orNoMessage(@Nullable String nullableString)
    {
        if (nullableString != null)
            return nullableString;
        return resources.getString("Error.NoMessageAlternative");
    }

    /**
     * Initializes the database structure.
     *
     * @since 0.0.1
     */
    private void initializeDatabaseStructure()
    {
        Connection connection = ConnectionHolder.getConnection();
        try
        {
            NounSQLFactory.setupDatabaseForNouns(connection);
        } catch (Throwable t)
        {
            splashException("StructureSetupFailure", t, 2);
        }
    }

    /**
     * Starts the application.
     * <p>
     * This method is to be called on the JavaFX application thread.
     *
     * @since 0.0.1
     */
    public void startApplication()
    {
        Task<Void> applicationStartTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                Platform.runLater(() -> {
                    updateProgress(0, 0);
                    updateMessage(resources.getString("ConnectingToDatabase"));
                });

                connectToDatabase();

                Platform.runLater(() -> {
                    updateProgress(1, 2);
                    updateMessage(resources.getString("InitializingDatabaseStructure"));
                });

                initializeDatabaseStructure();

                Platform.runLater(() -> {
                    updateProgress(2, 2);
                    updateMessage(resources.getString("OpeningMainWindow"));
                });

                Platform.runLater(SplashController.this::openMainWindow);

                Platform.runLater(pane.getScene().getWindow()::hide);
                return null;
            }
        };
        // This is why updateProgress() must be in JavaFX application thread, bind updated to JavaFX elements get called
        // in the same thread you update the original property in
        progress.progressProperty().bind(applicationStartTask.progressProperty());
        text.textProperty().bind(applicationStartTask.messageProperty());

        Thread th = new Thread(applicationStartTask, "Application Start Task");
        th.start();
    }

    /**
     * Opens the main window.
     * <p>
     * This method is to be called on the JavaFX application thread.
     *
     * @since 0.0.1
     */
    private void openMainWindow()
    {
        Stage stage = new Stage();
        try
        {
            Main.createOntoStage(stage);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        stage.show();
    }
}

