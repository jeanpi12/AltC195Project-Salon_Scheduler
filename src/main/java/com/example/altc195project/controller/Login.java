package com.example.altc195project.controller;

import com.example.altc195project.DAO.AppointmentDAO;
import com.example.altc195project.DAO.UserDAO;
import com.example.altc195project.models.Appointment;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the login screen.
 *
 * Handles user authentication, localization, logging of login attempts, and alerts for upcoming appointments.
 * Implements {@link Initializable} to perform initialization after FXML loading.
 *
 */
public class Login implements Initializable {

    /** Label displaying the login title, localized to the user's language. */
    @FXML private Label loginTitle;
    /** Label for the User ID input field, localized. */
    @FXML private Label labelUserID;
    /** Label for the Password input field, localized. */
    @FXML private Label labelPassword;
    /** Text field for entering the user login name. */
    @FXML private TextField txtFieldUserID;
    /** Password field for entering the user password. */
    @FXML private PasswordField txtFieldPassword;
    /** Button to submit login credentials. */
    @FXML private Button loginButton;
    /** Button to cancel and exit the application. */
    @FXML private Button cancelButton;
    /** Label displaying the system's default time zone. */
    @FXML private Label labelLocation;

    /** Resource bundle for localized UI text. */
    private ResourceBundle langBundle;

    /**
     * Initializes the controller after the FXML elements have been loaded.
     *
     * Loads the appropriate resource bundle based on the default {@link Locale}, falling back to English.
     * Sets UI text and displays the system default {@link ZoneId}.
     *
     *
     * @param url  the location used to resolve relative paths for the root object, or null if unknown
     * @param rb   the resource bundle for localization, not null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale locale = Locale.getDefault();
        try {
            langBundle = ResourceBundle.getBundle("lang", locale);
        } catch (Exception e) {
            langBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);
        }

        loginTitle.setText(langBundle.getString("login.title"));
        labelUserID.setText(langBundle.getString("login.username"));
        labelPassword.setText(langBundle.getString("login.password"));
        loginButton.setText(langBundle.getString("login.button"));
        cancelButton.setText(langBundle.getString("cancel.button"));

        labelLocation.setText(ZoneId.systemDefault().toString());
    }

    /**
     * Handles the login button action event.
     *
     * Validates the username and password, logs the attempt, shows alert messages,
     * checks for upcoming appointments, and switches to the main menu if successful.
     *
     *
     * @param event the {@link ActionEvent} triggered by clicking the login button
     */
    @FXML
    private void actionLoginButton(ActionEvent event) {
        String username = txtFieldUserID.getText().trim();
        String password = txtFieldPassword.getText();
        boolean success;
        String messageKey;

        if (!UserDAO.isLoginNameValid(username)) {
            success = false;
            messageKey = "error.unknown.user";
        } else if (!UserDAO.validateCredentials(username, password)) {
            success = false;
            messageKey = "error.bad.password";
        } else {
            success = true;
            messageKey = "login.success";
        }

        logAttempt(username, password, success);

        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(langBundle.getString("alert.title"));
        alert.setContentText(langBundle.getString(messageKey));
        alert.showAndWait();

        if (success) {
            checkUpcomingAppointments();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/altc195project/Menu.fxml"), langBundle);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(langBundle.getString("menu.title"));
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the cancel button action event by exiting the application.
     *
     * @param event the {@link ActionEvent} triggered by clicking the cancel button
     */
    @FXML
    private void actionCancelButton(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Logs a login attempt to "log.txt" in the application root.
     *
     * @param user    the username that was attempted
     * @param psw     the password that was attempted
     * @param success true if login succeeded, false otherwise
     */
    private void logAttempt(String user, String psw, boolean success) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        String record = String.format("%s | user: %s | pass: %s | success: %s%n", timestamp, user, psw, success);
        try (FileWriter fw = new FileWriter("log.txt", true)) {
            fw.write(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks for appointments starting within the next 15 minutes and displays an alert.
     *
     * Uses a lambda expression to filter upcoming appointments concisely.
     * Justification: The lambda improves readability and reduces boilerplate code compared to an explicit loop.
     *
     */
    private void checkUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusMinutes(15);
        List<Appointment> upcoming = AppointmentDAO.getAppointmentList()
                .stream()
                .filter(a -> {
                    LocalDateTime start = a.getStartAppointment();
                    return !start.isBefore(now) && start.isBefore(cutoff);
                })
                .collect(Collectors.toList());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(langBundle.getString("alert.upcoming.title"));
        if (upcoming.isEmpty()) {
            alert.setContentText(langBundle.getString("alert.no.upcoming"));
        } else {
            StringBuilder sb = new StringBuilder();
            upcoming.forEach(a -> sb.append(langBundle.getString("alert.upcoming.line"))
                    .append(a.getIdAppointment())
                    .append(" @ ")
                    .append(a.getStartAppointment().format(DateTimeFormatter.ofPattern("MM/dd uu HH:mm")))
                    .append("\n"));
            alert.setContentText(sb.toString());
        }
        alert.showAndWait();
    }
}
