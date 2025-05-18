/**
 * The {@code Appointment} class models an appointment entity in the system, supporting multiple
 * constructor signatures for creation, display, and reporting purposes. It encapsulates details
 * such as appointment ID, title, description, location, type, start/end times, and associations
 * with clients and stylists.
 * <p>
 * Instances of this class are used throughout the application for CRUD operations, display in
 * UI tables, and generating reports on appointment scheduling.
 */
package com.example.altc195project.models;

import com.example.altc195project.database.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {

    /** Unique identifier for the appointment. */
    private int idAppointment;

    /** Title of the appointment. */
    private String titleAppointment;

    /** Description of the appointment. */
    private String descrAppointment;

    /** Location where the appointment will take place. */
    private String locationAppointment;

    /** Type/category of the appointment. */
    private String typeAppointment;

    /** Used for report: total count of appointments by type. */
    private int totalAppointmentTypes;

    /** Start date and time of the appointment. */
    private LocalDateTime startAppointment;

    /** End date and time of the appointment. */
    private LocalDateTime endAppointment;

    /** Identifier of the associated client. */
    private int idClient;

    /** Identifier of the associated stylist. */
    private int idStyle;

    /** Name of the associated stylist (for display purposes). */
    private String stylistNameAppointment;

    /** Name of the associated client (for extended display). */
    private String clientNameAppointment;

    /**
     * Full CRUD constructor that initializes all fields, including raw client and stylist IDs.
     *
     * @param idAppointment        unique appointment ID
     * @param titleAppointment     appointment title
     * @param descrAppointment     appointment description
     * @param locationAppointment  appointment location
     * @param typeAppointment      appointment type/category
     * @param startAppointment     appointment start timestamp
     * @param endAppointment       appointment end timestamp
     * @param idClient             ID of the client associated with this appointment
     * @param idStyle              ID of the stylist associated with this appointment
     */
    public Appointment(int idAppointment,
                       String titleAppointment,
                       String descrAppointment,
                       String locationAppointment,
                       String typeAppointment,
                       LocalDateTime startAppointment,
                       LocalDateTime endAppointment,
                       int idClient,
                       int idStyle)
    {
        this.idAppointment       = idAppointment;
        this.titleAppointment    = titleAppointment;
        this.descrAppointment    = descrAppointment;
        this.locationAppointment = locationAppointment;
        this.typeAppointment     = typeAppointment;
        this.startAppointment    = startAppointment;
        this.endAppointment      = endAppointment;
        this.idClient            = idClient;
        this.idStyle             = idStyle;
    }

    /**
     * Constructor for CRUD/display use: includes stylist name for UI tables.
     * Client name is not included.
     *
     * @param idAppointment            appointment ID
     * @param titleAppointment         title
     * @param descrAppointment         description
     * @param locationAppointment      location
     * @param stylistNameAppointment   name of the stylist
     * @param typeAppointment          type/category
     * @param startAppointment         start timestamp
     * @param endAppointment           end timestamp
     * @param idClient                 client ID
     */
    public Appointment(int idAppointment,
                       String titleAppointment,
                       String descrAppointment,
                       String locationAppointment,
                       String stylistNameAppointment,
                       String typeAppointment,
                       LocalDateTime startAppointment,
                       LocalDateTime endAppointment,
                       int idClient)
    {
        this(idAppointment,
                titleAppointment,
                descrAppointment,
                locationAppointment,
                typeAppointment,
                startAppointment,
                endAppointment,
                idClient,
                0);
        this.stylistNameAppointment = stylistNameAppointment;
    }

    /**
     * Extended display constructor: includes both stylist name and client name for comprehensive UI.
     *
     * @param idAppointment            appointment ID
     * @param titleAppointment         title
     * @param descrAppointment         description
     * @param locationAppointment      location
     * @param stylistNameAppointment   stylist name
     * @param typeAppointment          type
     * @param startAppointment         start timestamp
     * @param endAppointment           end timestamp
     * @param idClient                 client ID
     * @param clientNameAppointment    client name
     */
    public Appointment(int idAppointment,
                       String titleAppointment,
                       String descrAppointment,
                       String locationAppointment,
                       String stylistNameAppointment,
                       String typeAppointment,
                       LocalDateTime startAppointment,
                       LocalDateTime endAppointment,
                       int idClient,
                       String clientNameAppointment)
    {
        this(idAppointment,
                titleAppointment,
                descrAppointment,
                locationAppointment,
                stylistNameAppointment,
                typeAppointment,
                startAppointment,
                endAppointment,
                idClient);
        this.clientNameAppointment = clientNameAppointment;
    }

    /**
     * Report constructor for stylist schedules: no stylistName supplied.
     *
     * @param idAppointment        appointment ID
     * @param titleAppointment     title
     * @param typeAppointment      type
     * @param descrAppointment     description
     * @param startAppointment     start timestamp
     * @param endAppointment       end timestamp
     * @param idClient             client ID
     */
    public Appointment(int idAppointment,
                       String titleAppointment,
                       String typeAppointment,
                       String descrAppointment,
                       LocalDateTime startAppointment,
                       LocalDateTime endAppointment,
                       int idClient)
    {
        this.idAppointment       = idAppointment;
        this.titleAppointment    = titleAppointment;
        this.typeAppointment     = typeAppointment;
        this.descrAppointment    = descrAppointment;
        this.startAppointment    = startAppointment;
        this.endAppointment      = endAppointment;
        this.idClient            = idClient;
    }

    /**
     * Report constructor for appointment counts by type.
     *
     * @param typeAppointment          appointment type
     * @param totalAppointmentTypes    total number of appointments of this type
     */
    public Appointment(String typeAppointment, int totalAppointmentTypes) {
        this.typeAppointment       = typeAppointment;
        this.totalAppointmentTypes = totalAppointmentTypes;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Getters & setters
    // ─────────────────────────────────────────────────────────────────────────────

    /** @return unique appointment ID */
    public int getIdAppointment() {
        return idAppointment;
    }

    /** @param idAppointment unique appointment ID */
    public void setIdAppointment(int idAppointment) {
        this.idAppointment = idAppointment;
    }

    /** @return appointment title */
    public String getTitleAppointment() {
        return titleAppointment;
    }

    /** @param titleAppointment title to set */
    public void setTitleAppointment(String titleAppointment) {
        this.titleAppointment = titleAppointment;
    }

    /** @return appointment description */
    public String getDescrAppointment() {
        return descrAppointment;
    }

    /** @param descrAppointment description to set */
    public void setDescrAppointment(String descrAppointment) {
        this.descrAppointment = descrAppointment;
    }

    /** @return appointment location */
    public String getLocationAppointment() {
        return locationAppointment;
    }

    /** @param locationAppointment location to set */
    public void setLocationAppointment(String locationAppointment) {
        this.locationAppointment = locationAppointment;
    }

    /** @return appointment type */
    public String getTypeAppointment() {
        return typeAppointment;
    }

    /** @param typeAppointment type to set */
    public void setTypeAppointment(String typeAppointment) {
        this.typeAppointment = typeAppointment;
    }

    /** @return start datetime */
    public LocalDateTime getStartAppointment() {
        return startAppointment;
    }

    /** @param startAppointment start datetime to set */
    public void setStartAppointment(LocalDateTime startAppointment) {
        this.startAppointment = startAppointment;
    }

    /** @return end datetime */
    public LocalDateTime getEndAppointment() {
        return endAppointment;
    }

    /** @param endAppointment end datetime to set */
    public void setEndAppointment(LocalDateTime endAppointment) {
        this.endAppointment = endAppointment;
    }

    /** @return client ID */
    public int getIdClient() {
        return idClient;
    }

    /** @param idClient client ID to set */
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    /** @return stylist ID */
    public int getIdStyle() {
        return idStyle;
    }

    /** @param idStyle stylist ID to set */
    public void setIdStyle(int idStyle) {
        this.idStyle = idStyle;
    }

    /** @return stylist name for display */
    public String getStylistNameAppointment() {
        return stylistNameAppointment;
    }

    /** @param stylistNameAppointment stylist name to set */
    public void setStylistNameAppointment(String stylistNameAppointment) {
        this.stylistNameAppointment = stylistNameAppointment;
    }

    /** @return client name for display */
    public String getClientNameAppointment() {
        return clientNameAppointment;
    }

    /** @param clientNameAppointment client name to set */
    public void setClientNameAppointment(String clientNameAppointment) {
        this.clientNameAppointment = clientNameAppointment;
    }

    /** @return total number of appointments of a given type */
    public int getTotalAppointmentTypes() {
        return totalAppointmentTypes;
    }

    /** @param totalAppointmentTypes total to set for reporting */
    public void setTotalAppointmentTypes(int totalAppointmentTypes) {
        this.totalAppointmentTypes = totalAppointmentTypes;
    }

    /**
     * Checks whether a proposed appointment time overlaps existing appointments for the same client.
     *
     * @param clientId      the ID of the client to check
     * @param proposedStart the proposed appointment start
     * @param proposedEnd   the proposed appointment end
     * @return {@code true} if an overlap exists, {@code false} otherwise
     * @throws RuntimeException if SQL error occurs
     */
    public static boolean timeOverlapCheck(int clientId,
                                           LocalDateTime proposedStart,
                                           LocalDateTime proposedEnd) {
        String sql = "SELECT COUNT(*) FROM appt "
                + "WHERE clientid = ? "
                + "AND (start < ? AND end > ?)";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.setTimestamp(2, Timestamp.valueOf(proposedEnd));
            ps.setTimestamp(3, Timestamp.valueOf(proposedStart));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking overlap", e);
        }
        return false;
    }

    /**
     * Generates a list of valid appointment times at 30-minute intervals.
     * Useful for populating time selection UI controls.
     *
     * @return an {@link ObservableList} of {@link LocalTime} in 30-minute increments
     */
    public static ObservableList<LocalTime> getTimes() {
        ObservableList<LocalTime> appointmentTimesList = FXCollections.observableArrayList();
        LocalTime start = LocalTime.of(1, 0);
        LocalTime end = LocalTime.MIDNIGHT.minusHours(1);

        while (start.isBefore(end.plusSeconds(2))) {
            appointmentTimesList.add(start);
            start = start.plusMinutes(30);
        }
        return appointmentTimesList;
    }
}
