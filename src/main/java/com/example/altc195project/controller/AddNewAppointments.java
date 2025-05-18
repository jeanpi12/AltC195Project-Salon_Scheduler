package com.example.altc195project.controller;

import com.example.altc195project.DAO.AppointmentDAO;
import com.example.altc195project.DAO.ClientDAO;
import com.example.altc195project.DAO.StylistDAO;
import com.example.altc195project.models.Appointment;
import com.example.altc195project.models.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the "Add New Appointment" screen.
 *
 * Handles user input for creating a new appointment, including:
 *
 *   Populating stylist and client dropdowns
 *   Time slot generation
 *   Validation against business hours and holidays
 *   Overlap checks
 *   Insertion into the database
 *
 *
 */
public class AddNewAppointments implements Initializable {

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
     * Initializes the Add New Appointment form.
     *
     * Populates:
     *
     *   Stylist dropdown using a lambda to map Stylist objects to their names,
     *       which is more concise and readable than a manual loop.
     *   Client dropdown using a method reference to extract client IDs,
     *       improving maintainability.
     *   Time slots at 30-minute intervals between 08:00 and 22:00.
     *
     *
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown.
     * @param rb  The resources used to localize the root object, or null if none.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        apptIDTextField.setText("Auto-Generated ID");

        // Fill stylist list
        stylistComboBox.setItems(
                StylistDAO.getAllStylists()
                        .stream()
                        .map(s -> s.getStylistName()) // lambda improves readability over a loop
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );

        // Fill client IDs
        clientComboBox.setItems(
                ClientDAO.getAllClients()
                        .stream()
                        .map(Client::getClientId) // method reference is concise and clear
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );

        // Fill time slots every 30 mins
        ObservableList<LocalTime> times = FXCollections.observableArrayList();
        LocalTime t = LocalTime.of(8, 0);
        while (!t.isAfter(LocalTime.of(22, 0))) {
            times.add(t);
            t = t.plusMinutes(30);
        }
        startTimeComboBox.setItems(times);
        endTimeComboBox.setItems(times);
    }

    /**
     * Event handler for the Save button.
     *
     * Validates that all fields are filled, checks business hours and holidays,
     * verifies no overlapping appointments for the same client, and then
     * writes the new appointment to the database.
     *
     *
     * @param event The action event triggered by clicking Save.
     */
    @FXML
    private void actionSaveButton(ActionEvent event) {
        try {
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

            if (title.isEmpty() || descr.isEmpty() || loc.isEmpty() || type.isEmpty() ||
                    sty == null || cid == null || sd == null || ed == null || st == null || en == null) {
                showError("All fields must be filled.");
                return;
            }

            LocalDateTime start = LocalDateTime.of(sd, st);
            LocalDateTime end   = LocalDateTime.of(ed, en);

            // Business hours & holiday check
            if (!isWithinBusiness(start, end)) {
                showError("Outside business hours or holiday.");
                return;
            }

            // Overlap check
            if (Appointment.timeOverlapCheck(cid, start, end)) {
                showError("Appointment overlaps existing.");
                return;
            }

            // Save to DB
            AppointmentDAO.addingAppointments(
                    title, descr, loc, type, start, end, cid,
                    StylistDAO.getStylistIdByName(sty)
            );
            showInfo("Appointment added.");
            switchScene("/com/example/altc195project/Appointments.fxml", "Appointments", event);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            showError("Failed to save appointment.");
        }
        catch (Exception e) {
            e.printStackTrace();
            showError("Unknown error.");
        }
    }

    /**
     * Event handler for the Cancel button.
     *
     * Discards any input and returns to the appointments overview.
     *
     *
     * @param event The action event triggered by clicking Cancel.
     */
    @FXML
    private void actionCancelButton(ActionEvent event) {
        switchScene("/com/example/altc195project/Appointments.fxml", "Appointments", event);
    }

    /**
     * Checks if the given appointment times fall within business hours (08:00–22:00 MST)
     * and are not on recognized holidays.
     *
     * @param start The local start date/time of the appointment.
     * @param end   The local end date/time of the appointment.
     * @return {@code true} if the appointment is within hours and not on a holiday.
     */
    public static boolean isWithinBusiness(LocalDateTime start, LocalDateTime end) {
        ZoneId mST = ZoneId.of("America/Denver");
        ZonedDateTime zStart = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(mST);
        ZonedDateTime zEnd   = end.atZone(ZoneId.systemDefault()).withZoneSameInstant(mST);
        LocalTime s = zStart.toLocalTime(), en = zEnd.toLocalTime();

        // Must be between 08:00 and 22:00
        if (s.isBefore(LocalTime.of(8, 0)) || en.isAfter(LocalTime.of(22, 0))) {
            return false;
        }

        // Holiday check
        return !isHoliday(zStart.toLocalDate()) && !isHoliday(zEnd.toLocalDate());
    }

    /**
     * Determines if a given date is a holiday as defined by the application
     * (New Year's Day, Independence Day, Thanksgiving). Adjusts for weekend observations.
     *
     * @param date The date to check.
     * @return {@code true} if the date is a recognized holiday.
     */
    public static boolean isHoliday(LocalDate date) {
        int year = date.getYear();
        List<LocalDate> hol = new ArrayList<>();

        // New Year's Day
        LocalDate ny = LocalDate.of(year, 1, 1);
        if (ny.getDayOfWeek() == DayOfWeek.SATURDAY)      ny = ny.minusDays(1);
        else if (ny.getDayOfWeek() == DayOfWeek.SUNDAY)  ny = ny.plusDays(1);
        hol.add(ny);

        // Independence Day
        LocalDate j4 = LocalDate.of(year, 7, 4);
        if (j4.getDayOfWeek() == DayOfWeek.SATURDAY)      hol.add(j4.minusDays(1));
        else if (j4.getDayOfWeek() == DayOfWeek.SUNDAY)  hol.add(j4.plusDays(1));
        else                                             hol.add(j4);

        // Thanksgiving (4th Thursday of November)
        LocalDate th = LocalDate.of(year, 11, 1);
        int count = 0;
        while (th.getMonthValue() == 11) {
            if (th.getDayOfWeek() == DayOfWeek.THURSDAY && ++count == 4) {
                break;
            }
            th = th.plusDays(1);
        }
        hol.add(th);
        hol.add(th.plusDays(1));  // The following Friday

        return hol.contains(date);
    }

    /**
     * Helper method to switch the UI scene.
     *
     * @param fxmlResource The FXML resource path to load.
     * @param title        The title for the new window.
     * @param event        The triggering ActionEvent, used to retrieve the current Stage.
     */
    private void switchScene(String fxmlResource, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResource));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Cannot load: " + fxmlResource);
        }
    }

    /**
     * Displays an error alert with the given message.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    /**
     * Displays an information alert with the given message.
     *
     * @param message The information message to display.
     */
    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}
