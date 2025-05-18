package com.example.altc195project.controller;

import com.example.altc195project.DAO.AppointmentDAO;
import com.example.altc195project.models.Appointment;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the "Appointments" screen.
 * Handles displaying all, weekly, or monthly appointments in a table,
 * and provides Add, Update, Delete, and navigation controls.
 *
 * Includes lambda expressions for concise event handling and cell formatting:
 *
 *   ToggleGroup listener to switch data view (all/week/month).
 *   Cell value factories that map LocalDateTime to String for display.
 *
 */
public class Appointments implements Initializable {

    /** TableView displaying Appointment objects. */
    @FXML private TableView<Appointment> appointmentTable;
    /** Column for appointment ID. */
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    /** Column for appointment title. */
    @FXML private TableColumn<Appointment, String> appointmentTitleCol;
    /** Column for appointment description. */
    @FXML private TableColumn<Appointment, String> appointmentDescrCol;
    /** Column for appointment location. */
    @FXML private TableColumn<Appointment, String> appointmentLocationCol;
    /** Column for stylist name. */
    @FXML private TableColumn<Appointment, String> appointmentStylistCol;
    /** Column for appointment type. */
    @FXML private TableColumn<Appointment, String> appointmentTypeCol;
    /** Column for start date/time as string. */
    @FXML private TableColumn<Appointment, String> appointmentStartCol;
    /** Column for end date/time as string. */
    @FXML private TableColumn<Appointment, String> appointmentEndCol;
    /** Column for client ID. */
    @FXML private TableColumn<Appointment, Integer> appointmentClientIdCol;

    /** RadioButton to show only this month's appointments. */
    @FXML private RadioButton monthAppointmentsButton;
    /** RadioButton to show only this week's appointments. */
    @FXML private RadioButton weekAppointmentsButton;
    /** RadioButton to show all appointments. */
    @FXML private RadioButton allAppointmentsButton;

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the ToggleGroup, table cell value factories, and default data.
     *
     * @param url            The location used to resolve relative paths for the root object
     * @param resourceBundle The resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Group the three radio buttons so only one can be selected at a time
        ToggleGroup group = new ToggleGroup();
        allAppointmentsButton.setToggleGroup(group);
        weekAppointmentsButton.setToggleGroup(group);
        monthAppointmentsButton.setToggleGroup(group);

        // Select "All" by default
        group.selectToggle(allAppointmentsButton);
        appointmentTable.setItems(AppointmentDAO.getAppointmentList());

        // Lambda expression: concise listener for toggle changes
        // Justification: using a lambda here keeps the toggle-switch logic inline and readable,
        // without the boilerplate of an anonymous inner class.
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == allAppointmentsButton) {
                appointmentTable.setItems(AppointmentDAO.getAppointmentList());
            } else if (newToggle == weekAppointmentsButton) {
                appointmentTable.setItems(AppointmentDAO.weeklyAppointments());
            } else {
                appointmentTable.setItems(AppointmentDAO.monthlyAppointments());
            }
        });

        // Standard cell-value factories for simple properties
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("idAppointment"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("titleAppointment"));
        appointmentDescrCol.setCellValueFactory(new PropertyValueFactory<>("descrAppointment"));
        appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("locationAppointment"));
        appointmentStylistCol.setCellValueFactory(new PropertyValueFactory<>("stylistNameAppointment"));
        appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeAppointment"));

        // Lambda expression: convert LocalDateTime to String for display
        // Justification: simplifies mapping of complex types directly in-line,
        // avoids creating a separate converter class.
        appointmentStartCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStartAppointment().toString())
        );
        appointmentEndCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getEndAppointment().toString())
        );

        appointmentClientIdCol.setCellValueFactory(new PropertyValueFactory<>("idClient"));

        // Ensure table initially shows all appointments
        loadAll();
    }

    /** Handler for the "All Appointments" RadioButton. */
    @FXML private void allAppointments(ActionEvent event) { loadAll(); }

    /** Handler for the "Week" RadioButton. */
    @FXML private void weekAppointments(ActionEvent event) { loadWeek(); }

    /** Handler for the "Month" RadioButton. */
    @FXML private void monthAppointments(ActionEvent event) { loadMonth(); }

    /**
     * Opens the Add New Appointment screen.
     *
     * @param event the ActionEvent triggered by clicking "Add"
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML private void actionAppointmentAdd(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/altc195project/AddNewAppointments.fxml")
        );
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Opens the Modify Appointment screen for the selected appointment.
     * If none is selected, shows an error alert.
     *
     * @param event the ActionEvent triggered by clicking "Update"
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML private void actionAppointmentUpdate(ActionEvent event) throws IOException {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an appointment to modify.");
            return;
        }
        ModifyAppointments.selected = selected;

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/altc195project/ModifyAppointments.fxml")
        );
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Modify Appointment");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Deletes the selected appointment after confirmation.
     * If none is selected, shows an error alert.
     *
     * @param event the ActionEvent triggered by clicking "Delete"
     */
    @FXML private void actionAppointmentDelete(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an appointment to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText(
                "Delete Appt ID " + selected.getIdAppointment() +
                        " of type '" + selected.getTypeAppointment() + "'?"
        );
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                AppointmentDAO.deleteAppointment(selected.getIdAppointment());
                showInfo("Deleted Appt ID " + selected.getIdAppointment());
                loadAll();
            }
        });
    }

    /**
     * Returns to the main menu screen.
     *
     * @param event the ActionEvent triggered by clicking "Back to Menu"
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML private void backMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/altc195project/Menu.fxml")
        );
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /** Loads all appointments into the table. */
    private void loadAll() {
        ObservableList<Appointment> list = AppointmentDAO.getAppointmentList();
        appointmentTable.setItems(list);
    }

    /** Loads only this week's appointments into the table. */
    private void loadWeek() {
        appointmentTable.setItems(AppointmentDAO.weeklyAppointments());
    }

    /** Loads only this month's appointments into the table. */
    private void loadMonth() {
        appointmentTable.setItems(AppointmentDAO.monthlyAppointments());
    }

    /**
     * Displays an error alert with the given message.
     *
     * @param message the error message to show
     */
    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    /**
     * Displays an information alert with the given message.
     *
     * @param message the info message to show
     */
    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}
