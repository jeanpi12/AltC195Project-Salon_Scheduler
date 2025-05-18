package com.example.altc195project.main;

import com.example.altc195project.database.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Entry point for the Scheduling Application.
 *
 * Manages the lifecycle of the database connection and
 * initializes the JavaFX login screen.
 *
 *
 *   @author Jean-Pierre Atiles
 *   @version 1.0
 *   @since 2025-05-11
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application by loading and displaying the login UI.
     *
     * This method is called by the JavaFX runtime after {@link #main(String[])}.
     * It loads {@code Login.fxml} from the application's resources,
     * sets up the primary stage, and shows it.
     *
     *
     * @param stage the primary stage for this application
     * @throws Exception if the FXML file cannot be found or loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        String fxmlPath = "/com/example/altc195project/Login.fxml";
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getResource(fxmlPath),
                        "FXML file not found: " + fxmlPath
                )
        );
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * The main entry point of the application.
     *
     * 1. Opens the MySQL database connection.
     * 2. Launches the JavaFX application, which invokes {@link #start(Stage)}.
     * 3. Closes the database connection when the application exits.
     *
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
