package com.example.altc195project.controller;

import com.example.altc195project.DAO.AppointmentDAO;
import com.example.altc195project.DAO.ClientDAO;
import com.example.altc195project.DAO.StylistDAO;
import com.example.altc195project.models.Appointment;
import com.example.altc195project.models.Stylist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the "Modify Appointment" screen. Responsible for
 * initializing UI components with existing appointment data,
 * handling user modifications, and persisting updates to the database.
 *
 * Lambdas and method references are used to transform collections:
 *
 *   Stylist::getStylistName as a method reference for readability when extracting names.
 *   c -> c.getClientId() as a lambda to map Client objects to their IDs explicitly.
 *
 */
public class ModifyAppointments implements Initializable {
    /**
     * Static reference to the appointment being modified.
     */
    public static Appointment selected;

    // --- injected fields from ModifyAppointments.fxml ---
    @FXML private TextField apptIDTextField;
    @FXML private TextField apptTitleTextField;
    @FXML private TextField apptDescrTextField;
    @FXML private TextField apptLocationTextField;
    @FXML private TextField apptTypeTextField;
    @FXML private ComboBox<String> stylistComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<LocalTime> startTimeComboBox;
    @FXML private ComboBox<LocalTime> endTimeComboBox;
    @FXML private ComboBox<Integer> clientComboBox;

    /**
     * Initializes UI controls:
     *
     *   Populates stylist names via a stream and method reference for conciseness.
     *   Populates client IDs via a stream and lambda for explicit mapping.
     *   Generates time slots every 30 minutes between 08:00 and 17:00.
     *   Pre-fills fields based on the static selected Appointment.
     *
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1) Populate stylist names
        ObservableList<String> stylists = StylistDAO.getAllStylists()
                .stream()
                .map(Stylist::getStylistName) // Method reference improves readability
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        stylistComboBox.setItems(stylists);

        // 2) Populate clients (by ID)
        ObservableList<Integer> clients = FXCollections.observableArrayList(
                ClientDAO.getAllClients()
                        .stream()
                        .map(c -> c.getClientId()) // Lambda allows explicit mapping to ID
                        .collect(Collectors.toList())
        );
        clientComboBox.setItems(clients);

        // 3) Populate time slots (every 30 minutes from 08:00 to 17:00)
        ObservableList<LocalTime> times = FXCollections.observableArrayList();
        LocalTime t = LocalTime.of(8, 0);
        while (!t.isAfter(LocalTime.of(17, 0))) {
            times.add(t);
            t = t.plusMinutes(30);
        }
        startTimeComboBox.setItems(times);
        endTimeComboBox.setItems(times);

        // 4) Pre-fill fields using the selected appointment data
        apptIDTextField.setText(String.valueOf(selected.getIdAppointment()));
        apptTitleTextField.setText(selected.getTitleAppointment());
        apptDescrTextField.setText(selected.getDescrAppointment());
        apptLocationTextField.setText(selected.getLocationAppointment());
        apptTypeTextField.setText(selected.getTypeAppointment());
        stylistComboBox.setValue(selected.getStylistNameAppointment());
        clientComboBox.setValue(selected.getIdClient());

        LocalDateTime st = selected.getStartAppointment();
        LocalDateTime en = selected.getEndAppointment();
        startDatePicker.setValue(st.toLocalDate());
        startTimeComboBox.setValue(st.toLocalTime());
        endDatePicker.setValue(en.toLocalDate());
        endTimeComboBox.setValue(en.toLocalTime());
    }

    /**
     * Handler for the Save button. Validates and updates the appointment via DAO,
     * then navigates back to the Appointments list on success.
     *
     * @param e the action event
     */
    @FXML private void actionSaveButton(ActionEvent e) {
        try {
            int id = selected.getIdAppointment();
            String title = apptTitleTextField.getText().trim();
            String descr = apptDescrTextField.getText().trim();
            String loc   = apptLocationTextField.getText().trim();
            String type  = apptTypeTextField.getText().trim();
            String sty   = stylistComboBox.getValue();
            Integer cid  = clientComboBox.getValue();
            LocalDate sd = startDatePicker.getValue();
            LocalDate ed = endDatePicker.getValue();
            LocalTime st = startTimeComboBox.getValue();
            LocalTime en = endTimeComboBox.getValue();

            LocalDateTime start = LocalDateTime.of(sd, st);
            LocalDateTime end   = LocalDateTime.of(ed, en);

            // Business hours and overlap checks
            if (!AddNewAppointments.isWithinBusiness(start, end)) {
                showError("Outside business hours or holiday.");
                return;
            }
            if (Appointment.timeOverlapCheck(cid, start, end)) {
                showError("Appointment overlaps existing.");
                return;
            }

            AppointmentDAO.updatingAppointments(
                    id, title, descr, loc, type, start, end,
                    cid, StylistDAO.getStylistIdByName(sty)
            );
            showInfo("Appointment updated.");
            switchScene("/com/example/altc195project/Appointments.fxml", "Appointments");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to update.");
        }
    }

    /**
     * Handler for the Cancel button. Discards changes and returns to the Appointments list.
     *
     * @param e the action event
     */
    @FXML private void actionCancelButton(ActionEvent e) {
        switchScene("/com/example/altc195project/Appointments.fxml", "Appointments");
    }

    /**
     * Utility to switch the scene to the given FXML resource.
     *
     * @param resourcePath the FXML file path
     * @param title        the window title
     */
    private void switchScene(String resourcePath, String title) {
        try {
            URL loc = getClass().getResource(resourcePath);
            if (loc == null) {
                throw new IllegalStateException("Cannot find FXML: " + resourcePath);
            }
            Parent root = FXMLLoader.load(loc);
            Stage stage = (Stage) apptIDTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            showError("Cannot load: " + resourcePath);
        }
    }

    /**
     * Displays an error alert with the given message.
     *
     * @param msg the error message
     */
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    /**
     * Displays an informational alert with the given message.
     *
     * @param msg the info message
     */
    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
