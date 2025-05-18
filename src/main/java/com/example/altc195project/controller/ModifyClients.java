/**
 * Controller class for modifying existing client records in the application.
 * Handles initializing the form with the client's current data, saving updates,
 * inactivating clients (deleting their appointments first), and cancelling modifications.
 */
package com.example.altc195project.controller;

import com.example.altc195project.DAO.AppointmentDAO;
import com.example.altc195project.DAO.ClientDAO;
import com.example.altc195project.database.JDBC;
import com.example.altc195project.models.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyClients implements Initializable {
    /**
     * The client selected for modification.
     */
    public static Client selectedClient;

    @FXML private TextField clientTextField;
    @FXML private TextField clientEmailTextField;
    @FXML private TextField clientHairColorTextField;
    @FXML private TextField clientPostalTextField;
    @FXML private TextField clientStateTextField;
    @FXML private ComboBox<String> clientCountryCombo;
    @FXML private TextField clientIDTextField;
    @FXML private CheckBox clientActiveCheckBox;

    /**
     * Initializes the controller by populating the country dropdown and pre-filling
     * all form fields with the selected client's current data.
     *
     * @param url            the location used to resolve relative paths for the root object
     * @param resourceBundle the resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate country ComboBox with distinct countries from the client table
        ObservableList<String> countries = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT country FROM client";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                countries.add(rs.getString("country"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        clientCountryCombo.setItems(countries);

        // Pre-fill form fields with the selected client's data
        clientIDTextField.setText(String.valueOf(selectedClient.getClientId()));
        clientTextField.setText(selectedClient.getClientName());
        clientEmailTextField.setText(selectedClient.getClientEmail());
        clientHairColorTextField.setText(selectedClient.getClientHairColor());
        clientPostalTextField.setText(String.valueOf(selectedClient.getClientPostalCode()));
        clientStateTextField.setText(selectedClient.getClientState());
        clientCountryCombo.setValue(selectedClient.getClientCountry());
        clientActiveCheckBox.setSelected(selectedClient.getClientActivity() == 1);
    }

    /**
     * Handles the Save button action. Validates input, deletes appointments if the client
     * is being inactivated, updates the client record in the database, and returns to
     * the Clients list.
     *
     * @param event the action event triggered by clicking Save
     */
    @FXML
    private void actionSaveButton(ActionEvent event) {
        // Validate that all required fields are filled
        if (clientTextField.getText().trim().isEmpty() ||
                clientEmailTextField.getText().trim().isEmpty() ||
                clientHairColorTextField.getText().trim().isEmpty() ||
                clientPostalTextField.getText().trim().isEmpty() ||
                clientStateTextField.getText().trim().isEmpty() ||
                clientCountryCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled out.");
            return;
        }

        boolean newActive = clientActiveCheckBox.isSelected();
        int clientId = selectedClient.getClientId();

        try {
            // If inactivating, delete all associated appointments first
            if (!newActive && selectedClient.getClientActivity() == 1) {
                AppointmentDAO.deleteAppointmentsByClientId(clientId);
            }

            // Update the client record
            ClientDAO.updateClient(
                    clientId,
                    clientTextField.getText().trim(),
                    clientEmailTextField.getText().trim(),
                    clientHairColorTextField.getText().trim(),
                    clientPostalTextField.getText().trim(),
                    clientStateTextField.getText().trim(),
                    clientCountryCombo.getValue(),
                    newActive ? 1 : 0
            );

            // Show custom confirmation message
            String msg = newActive
                    ? "Client updated successfully."
                    : "Client set to inactive — all their appointments have been deleted.";
            showAlert(Alert.AlertType.INFORMATION, msg);

            // Navigate back to the Clients list scene
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/altc195project/Clients.fxml"));
            Stage stage = (Stage) clientIDTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clients");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to update client.");
        }
    }

    /**
     * Handles the Cancel button action by returning to the Clients list without saving changes.
     *
     * @param event the action event triggered by clicking Cancel
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    private void actionCancelButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/altc195project/Clients.fxml"));
        Stage stage = (Stage) clientIDTextField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Clients");
        stage.centerOnScreen();
    }

    /**
     * Displays an alert dialog with the given type and message.
     *
     * @param type    the type of alert (e.g., INFORMATION, ERROR)
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message, ButtonType.OK).showAndWait();
    }
}
