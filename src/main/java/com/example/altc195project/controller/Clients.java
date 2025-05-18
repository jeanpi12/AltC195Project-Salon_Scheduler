package com.example.altc195project.controller;

import com.example.altc195project.DAO.ClientDAO;
import com.example.altc195project.models.Client;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Clients overview screen.
 *
 * Manages the display and CRUD operations for client records,
 * including adding, modifying, and deleting (inactivating) clients.
 */
public class Clients implements Initializable {

    /** TableView displaying all clients. */
    @FXML private TableView<Client> clientTable;

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up table columns and loads initial data.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure table columns based on Client property names
        clientTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("clientEmail"));
        clientTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("clientHairColor"));
        clientTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("clientPostalCode"));
        clientTable.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("clientActivity"));
        clientTable.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("clientState"));
        clientTable.getColumns().get(7).setCellValueFactory(new PropertyValueFactory<>("clientCountry"));

        // Load all clients into the table
        refreshTable();
    }

    /**
     * Handles the Add button action: opens the Add Client screen.
     *
     * @param event The action event triggered by clicking the Add button.
     */
    @FXML
    private void actionClientAdd(ActionEvent event) {
        switchScene("/com/example/altc195project/AddClients.fxml", "Add Client");
    }

    /**
     * Handles the Update button action: opens the Modify Client screen
     * for the currently selected client.
     *
     * @param event The action event triggered by clicking the Update button.
     */
    @FXML
    private void actionClientUpdate(ActionEvent event) {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a client to modify.");
            return;
        }
        // Pass selected client via static field to the ModifyClients controller
        ModifyClients.selectedClient = selected;
        switchScene("/com/example/altc195project/ModifyClients.fxml", "Modify Client");
    }

    /**
     * Handles the Delete button action: inactivates the selected client
     * after confirming, and refreshes the table.
     *
     * Appointments for the client are deleted before inactivation.
     *
     * @param event The action event triggered by clicking the Delete button.
     */
    @FXML
    public void actionClientDelete(ActionEvent event) {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a client to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete \u201C" + selected.getClientName() + "\u201D?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                // Delete or inactivate client
                ClientDAO.deleteClient(selected.getClientId());
                // Refresh view
                refreshTable();
            }
        });
    }

    /**
     * Handles the Back to Menu button action: returns to the main menu screen.
     *
     * @param event The action event triggered by clicking the Back to Menu button.
     */
    @FXML
    private void backMainMenu(ActionEvent event) {
        switchScene("/com/example/altc195project/Menu.fxml", "Main Menu");
    }

    /**
     * Reloads the client data from the database into the table.
     */
    private void refreshTable() {
        ObservableList<Client> clients = ClientDAO.getAllClients();
        clientTable.setItems(clients);
        clientTable.refresh();
    }

    /**
     * Displays an error alert with the specified message.
     *
     * @param msg The error message to display.
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Displays an information alert with the specified message.
     *
     * @param msg The information message to display.
     */
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Utility method to switch scenes.
     *
     * @param fxmlPath The path to the FXML file for the new scene.
     * @param title    The title of the new stage window.
     */
    private void switchScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
