package com.example.altc195project.controller;

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
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the "Add Client" screen.
 *
 * Presents the form for entering a new client’s details:
 * name, email, hair color, postal code, state/province, country, and active status.
 *
 *
 * On initialization, the country combo box is populated from distinct
 * values in the client table, the ID field is set to "Auto-Generated",
 * and the Active checkbox is selected by default.
 *
 *
 * @author Jean-Pierre Atiles
 * @version 1.0
 * @since 2025-05-11
 */

public class AddClients implements Initializable {
    /** Text field for the client’s full name. */
    @FXML private TextField clientTextField;

    /** Text field for the client’s email address. */
    @FXML private TextField clientEmailTextField;

    /** Text field for the client’s hair color. */
    @FXML private TextField clientHairColorTextField;

    /** Text field for the client’s postal code. */
    @FXML private TextField clientPostalTextField;

    /** Text field for the client’s state or province. */
    @FXML private TextField clientStateTextField;

    /** Combo box for selecting the client’s country. */
    @FXML private ComboBox<String> clientCountryCombo;

    /** Text field for the client’s ID (auto-generated). */
    @FXML private TextField clientIDTextField;

    /** Check box indicating whether the client is active. */
    @FXML private CheckBox clientActiveCheckBox;


    /**
     * Initializes the Add Client form.
     *
     *   Loads distinct country values from the database into the country combo box.
     *   Sets the client ID field to "Auto-Generated".
     *   Selects the Active checkbox by default.
     *
     *
     * @param url  the location used to resolve relative paths for the root object; may be null
     * @param rb   the resource bundle for localization; may be null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate country ComboBox from DB
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

        clientIDTextField.setText("Auto-Generated");
        clientActiveCheckBox.setSelected(true);
    }

    /**
     * Handles the Save button click.
     *
     * Validates that all fields are filled; if so, inserts a new client
     * record into the database with the selected active status.
     * Displays an information alert on success or an error alert on failure,
     * then returns to the clients overview screen.
     *
     *
     * @param event the action event triggered by clicking the Save button
     */
    @FXML
    private void actionSaveButton(ActionEvent event) {
        // Validate inputs
        String name = clientTextField.getText().trim();
        String email = clientEmailTextField.getText().trim();
        String hairColor = clientHairColorTextField.getText().trim();
        String postalCode = clientPostalTextField.getText().trim();
        String state = clientStateTextField.getText().trim();
        String country = clientCountryCombo.getValue();
        boolean active = clientActiveCheckBox.isSelected();

        if (name.isEmpty() || email.isEmpty() || hairColor.isEmpty()
                || postalCode.isEmpty() || state.isEmpty() || country == null) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled out.");
            return;
        }

        try {
            ClientDAO.addClient(
                    name,
                    email,
                    hairColor,
                    postalCode,           // still a String
                    state,
                    country,
                    active ? 1 : 0
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to add client:\n" + ex.getMessage());
            return;
        }

        // custom confirmation message
        if (active) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Client \"" + name + "\" added successfully.");
        } else {
            showAlert(Alert.AlertType.INFORMATION,
                    "Client \"" + name + "\" added as INACTIVE. (No appointments exist yet.)");
        }

        // go back to Clients list
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/altc195project/Clients.fxml"));
            Stage stage = (Stage) clientTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clients");
            stage.centerOnScreen();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    /**
     * Handles the Cancel button click.
     *
     * Discards any input and navigates back to the clients overview screen.
     *
     *
     * @param event the action event triggered by clicking the Cancel button
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    private void actionCancelButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/altc195project/Clients.fxml"));
        Stage stage = (Stage) clientTextField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Clients");
        stage.centerOnScreen();
    }

    /**
     * Displays an alert dialog of the given type with the provided message.
     *
     * @param type    the type of alert (e.g., ERROR, INFORMATION)
     * @param message the content text to display in the alert
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}