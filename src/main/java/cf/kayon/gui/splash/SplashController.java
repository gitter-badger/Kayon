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

import cf.kayon.core.KayonContext;
import cf.kayon.core.util.ConfigurationUtil;
import cf.kayon.gui.FxUtil;
import cf.kayon.gui.main.Main;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Thread.interrupted;
import static java.text.MessageFormat.format;

/**
 * Controller for the splash screen.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class SplashController
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(SplashController.class);

    @FXML
    ResourceBundle resources;

    @FXML
    private ProgressBar progress;

    @FXML
    private Pane pane;

    @FXML
    private Text text;

    @FXML
    private ImageView view;

    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * Makes the application-wide connection to the database.
     * <p>
     * To be called on the Application Startup Task thread.
     * Depends on {@link #loadConfig()}.
     *
     * @param config The Config object to use to get the values (url, user, password) for connecting to the database.
     * @return A Connection.
     * @since 0.0.1
     */
    @NotNull
    private Connection connectToDatabase(@NotNull Config config)
    {
        LOGGER.info("Connecting to database");
        try
        {
            Properties info = ConfigurationUtil.toProperties(config.getConfig("database.info").entrySet());
            String url = config.getString("database.url");
            LOGGER.info(" url: " + url);
            LOGGER.info(" info: " + ConfigurationUtil
                    .toStringPasswordAware(info, config.getInt("database.log.mode"), config.getString("database.log.algorithm"),
                                           config.getString("database.log.charset"), config.getString("database.log.replacement")));
            return DriverManager.getConnection(url, info);
        } catch (Throwable t)
        {
            splashException("ConnectionFailure", t);
            throw new RuntimeException(t);
        }
    }

    public void initialize()
    {
        //noinspection HardcodedFileSeparator
        FxUtil.LOGO.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1d)
            {
                progressIndicator.setVisible(false);
                FxUtil.initLogo((Stage) pane.getScene().getWindow());
            }
        });
        // in case image was already 100% loaded before listener was registered
        progressIndicator.setVisible(FxUtil.LOGO.getProgress() != 1d);
        view.setImage(FxUtil.LOGO);
    }

    /**
     * Loads the configuration file(s).
     * <p>
     * To be called on the Application Startup Task thread.
     *
     * @return The loaded config.
     * @since 0.2.0
     */
    @NotNull
    private Config loadConfig()
    {
        LOGGER.info("Loading config");
        try
        {
            return ConfigFactory.load(ConfigFactory.parseFileAnySyntax(new File("Kayon")));
        } catch (Throwable t)
        {
            splashException("ConfigLoadFailure", t);
            throw t;
        }
    }

    /**
     * Configures the application with the config loaded in {@link #loadConfig()}.
     * <p>
     * Depends on {@link #loadConfig()} and {@link #connectToDatabase(Config)}.
     *
     * @param config     The configuration to use.
     * @param connection The connection to use.
     * @since 0.2.0
     */
    private void configureApplication(@NotNull Connection connection, @NotNull Config config)
    {
        LOGGER.info("Configuring application");
        try
        {
            FxUtil.context = new KayonContext(connection, config);
            FxUtil.executor = new ThreadPoolExecutor(config.getInt("gui.executor.poolSize"),
                                                     config.getInt("gui.executor.poolSize"),
                                                     config.getDuration("gui.executor.keepAliveTime", TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS,
                                                     new LinkedBlockingQueue<>())
            {
                @Override
                protected void afterExecute(Runnable r, Throwable t)
                {
                    super.afterExecute(r, t);
                    if (t == null && r instanceof Future<?>)
                    {
                        try
                        {
                            Future<?> future = (Future<?>) r;
                            if (future.isDone())
                            {
                                future.get();
                            }
                        } catch (CancellationException ce)
                        {
                            t = ce;
                        } catch (ExecutionException ee)
                        {
                            t = ee.getCause();
                        } catch (InterruptedException ie)
                        {
                            Thread.currentThread().interrupt(); // ignore/reset
                        }
                    }
                    if (t != null)
                    {
                        // Log any exceptions thrown in the executor
                        LOGGER.error("Exception in FX executor occured!", t);
                    }
                }
            };
            FxUtil.executor.allowCoreThreadTimeOut(true);
        } catch (Throwable t)
        {
            splashException("ApplicationConfigureFailure", t);
            throw t;
        }
    }

    /**
     * Shows an exception alert.
     * <p>
     * NOT to be called on the JavaFX Application Thread, since the current thread will be interrupted.
     *
     * @param resourceBundlePackage The resource bundle package name of the error.
     * @param t                     The throwable to inform the user about.
     * @since 0.0.1
     */
    private void splashException(@NotNull String resourceBundlePackage, @NotNull Throwable t)
    {
        checkNotEmpty(resourceBundlePackage);
        checkNotNull(t);
        LOGGER.error("Splash screen exception occurred (resourceBundlePackage: " + resourceBundlePackage + ")", t);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            String className = t.getClass().getCanonicalName();
            String unLocalizedMessage = orNoMessage(t.getMessage());

            alert.setTitle(format(resources.getString("Error." + resourceBundlePackage + ".Title"), className, unLocalizedMessage));
            alert.setHeaderText(format(resources.getString("Error." + resourceBundlePackage + ".HeaderText"), className, unLocalizedMessage));
            alert.setContentText(format(resources.getString("Error." + resourceBundlePackage + ".ContentText"), className, unLocalizedMessage));

            Stage stage = (Stage) pane.getScene().getWindow();
            stage.setAlwaysOnTop(false);

            alert.initOwner(pane.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
            Platform.exit();
        });
        Thread.currentThread().interrupt(); // Halt the task thread execution
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
     * <p>
     * Depends on {@link #configureApplication(Connection, Config)}.
     *
     * @since 0.0.1
     */
    private void initializeDatabaseStructure()
    {
        LOGGER.info("Initializing database structure");
        try
        {
            FxUtil.context.getNounSQLFactory().setupDatabaseForNouns();
            FxUtil.context.getNounSQLFactory().compileStatements();
            if (FxUtil.context.getConfig().getBoolean("debug.gui.startupException"))
                throw new RuntimeException();
        } catch (Throwable t)
        {
            splashException("StructureSetupFailure", t);
            throw new RuntimeException(t);
        }
    }

    /**
     * Starts the application.
     * <p>
     * This method does not have to be called on the JavaFX application thread, but it can be.
     *
     * @since 0.0.1
     */
    public void startApplication()
    {
        LOGGER.info("Starting application");
        Task<Void> applicationStartTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                if (interrupted()) return null;
                Platform.runLater(() -> {
                    updateProgress(0, 5);
                    updateMessage(resources.getString("LoadingConfig"));
                });

                if (interrupted()) return null;
                Config config = loadConfig();

                if (interrupted()) return null;
                Platform.runLater(() -> {
                    updateProgress(1, 5);
                    updateMessage(resources.getString("ConnectingToDatabase"));
                });

                if (interrupted()) return null;
                Connection connection = connectToDatabase(config);

                if (interrupted()) return null;
                Platform.runLater(() -> {
                    updateProgress(2, 5);
                    updateMessage(resources.getString("ConfiguringApplication"));
                });

                if (interrupted()) return null;
                configureApplication(connection, config);

                if (interrupted()) return null;
                Platform.runLater(() -> {
                    updateProgress(3, 5);
                    updateMessage(resources.getString("InitializingDatabaseStructure"));
                });

                if (interrupted()) return null;
                initializeDatabaseStructure();

                if (interrupted()) return null;
                Platform.runLater(() -> {
                    updateProgress(4, 5);
                    updateMessage(resources.getString("OpeningMainWindow"));
                });

                if (interrupted()) return null;
                Platform.runLater(SplashController.this::openMainWindow);

                if (interrupted()) return null;
                Platform.runLater(pane.getScene().getWindow()::hide);
                return null;
            }
        };
        // This is why updateProgress() must be in JavaFX application thread, bind updated to JavaFX elements get called
        // in the same thread you update the original property in
        progress.progressProperty().bind(applicationStartTask.progressProperty());
        text.textProperty().bind(applicationStartTask.messageProperty());

        Thread th = new Thread(applicationStartTask, "Application Start Task"); // The first and last time of using new Thread() to execute a task
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
        LOGGER.info("Opening main window");
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

