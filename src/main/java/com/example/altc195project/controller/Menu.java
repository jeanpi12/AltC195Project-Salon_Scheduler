package com.example.altc195project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;

/**
 * Controller for the main menu scene.
 * Provides navigation to the Clients, Appointments, and Reports sections,
 * as well as functionality to exit the application.
 */
public class Menu {

    /**
     * Navigates to the Clients management view.
     * Loads the Clients.fxml layout and sets it on the current stage.
     *
     * @param event the ActionEvent triggered by the Clients button
     */
    @FXML
    private void actionMenuClients(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/altc195project/Clients.fxml")
            );
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clients");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the Appointments management view.
     * Loads the Appointments.fxml layout and sets it on the current stage.
     *
     * @param event the ActionEvent triggered by the Appointments button
     */
    @FXML
    private void actionMenuAppointments(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/altc195project/Appointments.fxml")
            );
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Appointments");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the Reports view.
     * Loads the Reports.fxml layout and sets it on the current stage.
     *
     * @param event the ActionEvent triggered by the Reports button
     */
    @FXML
    private void actionMenuReports(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/altc195project/Reports.fxml")
            );
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reports");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application gracefully by calling Platform.exit().
     *
     * @param event the ActionEvent triggered by the Exit button
     */
    @FXML
    private void actionMenuExit(ActionEvent event) {
        Platform.exit();
    }
}
