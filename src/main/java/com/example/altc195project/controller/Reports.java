package com.example.altc195project.controller;

import com.example.altc195project.DAO.AppointmentDAO;
import com.example.altc195project.DAO.ClientDAO;
import com.example.altc195project.DAO.StylistDAO;
import com.example.altc195project.models.Appointment;
import com.example.altc195project.models.Stylist;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Reports screen, which provides three different views:
 *
 *   Total client appointments by type and by week.
 *   Stylist schedules based on the selected stylist.
 *   Client activity status (active vs inactive).
 *
 */
public class Reports implements Initializable {

    /** TableView for showing appointment counts by type. */
    @FXML private TableView<Appointment> typeTableView;
    /** Column displaying the appointment type name. */
    @FXML private TableColumn<Appointment, String> typeCol;
    /** Column displaying the total number of appointments of each type. */
    @FXML private TableColumn<Appointment, Integer> typeTotalCol;

    /** TableView for showing appointment counts by week. */
    @FXML private TableView<Appointment> weekTableView;
    /** Column displaying the appointment type name for weekly counts. */
    @FXML private TableColumn<Appointment, String> weekCol;
    /** Column displaying the total appointments per week per type. */
    @FXML private TableColumn<Appointment, Integer> weekTotalCol;

    /** TableView for stylist schedules. */
    @FXML private TableView<Appointment> stylistScheduleTable;
    /** Column for appointment ID in stylist schedule. */
    @FXML private TableColumn<Appointment, Integer> stylistApptIdCol;
    /** Column for appointment title in stylist schedule. */
    @FXML private TableColumn<Appointment, String> stylistTitleCol;
    /** Column for appointment type in stylist schedule. */
    @FXML private TableColumn<Appointment, String> stylistTypeCol;
    /** Column for appointment description in stylist schedule. */
    @FXML private TableColumn<Appointment, String> stylistDescrCol;
    /** Column for appointment start time in stylist schedule. */
    @FXML private TableColumn<Appointment, String> stylistStartCol;
    /** Column for appointment end time in stylist schedule. */
    @FXML private TableColumn<Appointment, String> stylistEndCol;
    /** Column for client ID in stylist schedule. */
    @FXML private TableColumn<Appointment, Integer> stylistClientIdCol;
    /** ComboBox for selecting a stylist to filter the schedule. */
    @FXML private ComboBox<Stylist> stylistCombo;

    /** TableView for client activity status report. */
    @FXML private TableView<Appointment> clientActivityTable;
    /** Column for activity status label (active/inactive). */
    @FXML private TableColumn<Appointment, String> activityStatusCol;
    /** Column for the total number of clients per activity status. */
    @FXML private TableColumn<Appointment, Integer> activityTotalCol;

    /**
     * Initializes all three report tabs:
     *
     *   Tab 1: loads appointment counts by type and by week.
     *   Tab 2: sets up stylist schedule table and its listener.
     *   Tab 3: loads client activity status counts.
     *
     *
     * @param location  the URL location of the FXML, or null if not known
     * @param resources the ResourceBundle for localization, or null if not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Tab 1: Total Client Appointments by Type ---
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeAppointment"));
        typeTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAppointmentTypes"));
        typeTableView.setItems(AppointmentDAO.appointmentType());

        // --- Tab 1: Appointments by Week ---
        weekCol.setCellValueFactory(new PropertyValueFactory<>("typeAppointment"));
        weekTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAppointmentTypes"));
        weekTableView.setItems(AppointmentDAO.appointmentTypeWeek());

        // --- Tab 2: Stylist Schedules ---
        stylistApptIdCol.setCellValueFactory(new PropertyValueFactory<>("idAppointment"));
        stylistTitleCol.setCellValueFactory(new PropertyValueFactory<>("titleAppointment"));
        stylistTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeAppointment"));
        stylistDescrCol.setCellValueFactory(new PropertyValueFactory<>("descrAppointment"));
        stylistStartCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStartAppointment().toString())
        );
        /*
         * Lambda justification: Instead of creating an anonymous Callback class to convert
         * LocalDateTime to String for display, we use a concise lambda expression
         * that wraps the toString() result in a ReadOnlyStringWrapper.
         */
        stylistEndCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getEndAppointment().toString())
        );
        // Same justification as above lambda for end time.

        stylistClientIdCol.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        stylistCombo.setItems(StylistDAO.getAllStylists());
        stylistCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                stylistScheduleTable.setItems(
                        AppointmentDAO.getAppointmentsByStylist(sel.getStylistId())
                );
            }
        });
        /*
         * Lambda justification: Using a ChangeListener lambda here keeps the code inline
         * and readable, without the overhead of defining a separate listener class.
         */

        // --- Tab 3: Client Activity Status ---
        activityStatusCol.setCellValueFactory(new PropertyValueFactory<>("typeAppointment"));
        activityTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAppointmentTypes"));
        clientActivityTable.setItems(ClientDAO.getClientActivityReport());
    }

    /**
     * Handles the "Back to Menu" button click, returning the user to the main menu screen.
     *
     * @param event the ActionEvent triggered by clicking the button
     * @throws Exception if the FXML file cannot be loaded
     */
    @FXML
    private void backMainMenu(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/altc195project/Menu.fxml"));
        Stage stage = (Stage)((Control)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }
}
