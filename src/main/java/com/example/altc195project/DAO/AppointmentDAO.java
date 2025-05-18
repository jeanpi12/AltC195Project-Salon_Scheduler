package com.example.altc195project.DAO;

import com.example.altc195project.database.JDBC;
import com.example.altc195project.models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object for {@link Appointment} entities.
 * Provides CRUD operations and specialized queries for appointments,
 * including reports by time period and type.
 */
public class AppointmentDAO {

    /**
     * Retrieves all appointments along with client and stylist names.
     *
     * @return an {@link ObservableList} of all {@link Appointment} objects in the database
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> getAppointmentList() {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        String sql =
                "SELECT "
                        + "  a.idappt, a.title, a.descr, a.location, a.type, "
                        + "  a.start, a.end, "
                        + "  a.clientid, c.name AS clientName, "
                        + "  a.stylid, s.name AS stylistName "
                        + "FROM appt a "
                        + "JOIN client c ON a.clientid = c.idclient "
                        + "JOIN stylist s ON a.stylid = s.idstylist "
                        + "ORDER BY a.idappt";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("idappt"),
                        rs.getString("title"),
                        rs.getString("descr"),
                        rs.getString("location"),
                        rs.getString("stylistName"),
                        rs.getString("type"),
                        rs.getTimestamp("start").toLocalDateTime(),
                        rs.getTimestamp("end").toLocalDateTime(),
                        rs.getInt("clientid")
                );
                appt.setClientNameAppointment(rs.getString("clientName"));
                appointmentList.add(appt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appointmentList;
    }

    /**
     * Updates an existing appointment in the database.
     *
     * @param idAppointment the ID of the appointment to update
     * @param title         new title text
     * @param descr         new description text
     * @param location      new location text
     * @param type          new appointment type
     * @param start         new start date/time
     * @param end           new end date/time
     * @param clientId      associated client ID
     * @param stylistId     associated stylist ID
     * @throws RuntimeException if a SQL exception occurs during the update
     */
    public static void updatingAppointments(int idAppointment,
                                            String title, String descr,
                                            String location, String type,
                                            LocalDateTime start, LocalDateTime end,
                                            int clientId, int stylistId) {
        String sql =
                "UPDATE appt SET "
                        + "title = ?, descr = ?, location = ?, type = ?, "
                        + "start = ?, end = ?, "
                        + "clientid = ?, stylid = ? "
                        + "WHERE idappt = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, descr);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, Timestamp.valueOf(start));
            ps.setTimestamp(6, Timestamp.valueOf(end));
            ps.setInt(7, clientId);
            ps.setInt(8, stylistId);
            ps.setInt(9, idAppointment);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new appointment into the database.
     *
     * @param title     appointment title
     * @param descr     appointment description
     * @param location  appointment location
     * @param type      appointment type
     * @param start     appointment start date/time
     * @param end       appointment end date/time
     * @param clientId  associated client ID
     * @param stylistId associated stylist ID
     * @throws SQLException if a SQL exception occurs during insertion
     */
    public static void addingAppointments(String title, String descr,
                                          String location, String type,
                                          LocalDateTime start, LocalDateTime end,
                                          int clientId, int stylistId) throws SQLException {
        String sql =
                "INSERT INTO appt "
                        + "(title, descr, location, type, start, end, clientid, stylid) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, descr);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, Timestamp.valueOf(start));
            ps.setTimestamp(6, Timestamp.valueOf(end));
            ps.setInt(7, clientId);
            ps.setInt(8, stylistId);
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves appointments scheduled for the current week.
     *
     * @return an {@link ObservableList} of {@link Appointment} objects for the current week
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> weeklyAppointments() {
        ObservableList<Appointment> weekList = FXCollections.observableArrayList();
        String sql =
                "SELECT "
                        + "  a.idappt, a.title, a.descr, a.location, a.type, "
                        + "  a.start, a.end, a.clientid, "
                        + "  a.stylid, s.name AS stylistName "
                        + "FROM appt a "
                        + "JOIN stylist s ON a.stylid = s.idstylist "
                        + "WHERE YEARWEEK(a.start, 1) = YEARWEEK(NOW(), 1) "
                        + "ORDER BY a.idappt";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("idappt"),
                        rs.getString("title"),
                        rs.getString("descr"),
                        rs.getString("location"),
                        rs.getString("stylistName"),
                        rs.getString("type"),
                        rs.getTimestamp("start").toLocalDateTime(),
                        rs.getTimestamp("end").toLocalDateTime(),
                        rs.getInt("clientid")
                );
                weekList.add(appt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return weekList;
    }

    /**
     * Deletes an appointment by its ID.
     *
     * @param idAppointment the ID of the appointment to delete
     */
    public static void deleteAppointment(int idAppointment) {
        String sql = "DELETE FROM appt WHERE idappt = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, idAppointment);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves appointments scheduled for the current month.
     *
     * @return an {@link ObservableList} of {@link Appointment} objects for the current month
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> monthlyAppointments() {
        ObservableList<Appointment> monthList = FXCollections.observableArrayList();
        String sql =
                "SELECT "
                        + "  a.idappt, a.title, a.descr, a.location, a.type, "
                        + "  a.start, a.end, a.clientid, "
                        + "  a.stylid, s.name AS stylistName "
                        + "FROM appt a "
                        + "JOIN stylist s ON a.stylid = s.idstylist "
                        + "WHERE MONTH(a.start) = MONTH(NOW()) "
                        + "ORDER BY a.idappt";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("idappt"),
                        rs.getString("title"),
                        rs.getString("descr"),
                        rs.getString("location"),
                        rs.getString("stylistName"),
                        rs.getString("type"),
                        rs.getTimestamp("start").toLocalDateTime(),
                        rs.getTimestamp("end").toLocalDateTime(),
                        rs.getInt("clientid")
                );
                monthList.add(appt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return monthList;
    }

    /**
     * Retrieves appointments associated with a specific client.
     *
     * @param clientId the ID of the client whose appointments are requested
     * @return an {@link ObservableList} of the client's {@link Appointment} objects
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> clientAppointments(int clientId) {
        ObservableList<Appointment> clientList = FXCollections.observableArrayList();
        String sql =
                "SELECT "
                        + "  a.idappt, a.title, a.descr, a.location, a.type, "
                        + "  a.start, a.end, a.stylid, s.name AS stylistName "
                        + "FROM appt a "
                        + "JOIN stylist s ON a.stylid = s.idstylist "
                        + "WHERE a.clientid = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment appt = new Appointment(
                            rs.getInt("idappt"),
                            rs.getString("title"),
                            rs.getString("descr"),
                            rs.getString("location"),
                            rs.getString("stylistName"),
                            rs.getString("type"),
                            rs.getTimestamp("start").toLocalDateTime(),
                            rs.getTimestamp("end").toLocalDateTime(),
                            clientId
                    );
                    clientList.add(appt);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clientList;
    }

    /**
     * Generates a report of total appointment counts grouped by type (all-time).
     *
     * @return an {@link ObservableList} of {@link Appointment} objects with type and count fields populated
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> appointmentType() {
        ObservableList<Appointment> appointmentTypeList = FXCollections.observableArrayList();
        String sql = "SELECT type, COUNT(*) AS NUM FROM appt GROUP BY type";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment reportRow = new Appointment(
                        rs.getString("type"),
                        rs.getInt("NUM")
                );
                appointmentTypeList.add(reportRow);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appointmentTypeList;
    }

    /**
     * Generates a report of appointment counts grouped by type for the current week.
     *
     * @return an {@link ObservableList} of {@link Appointment} objects with type and count fields populated
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> appointmentTypeWeek() {
        ObservableList<Appointment> appointmentTypeWeekList = FXCollections.observableArrayList();
        String sql =
                "SELECT type, COUNT(*) AS NUM "
                        + "FROM appt "
                        + "WHERE YEARWEEK(start, 1) = YEARWEEK(NOW(), 1) "
                        + "GROUP BY type";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment reportRow = new Appointment(
                        rs.getString("type"),
                        rs.getInt("NUM")
                );
                appointmentTypeWeekList.add(reportRow);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appointmentTypeWeekList;
    }

    /**
     * Retrieves all appointments for a given stylist.
     *
     * @param stylistId ID of the stylist whose schedule is requested
     * @return an {@link ObservableList} of {@link Appointment} objects for the stylist
     * @throws RuntimeException if a SQL exception occurs during the query
     */
    public static ObservableList<Appointment> getAppointmentsByStylist(int stylistId) {
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        String sql = "SELECT idappt, title, type, descr, start, end, clientid "
                + "FROM appt WHERE stylid = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, stylistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment appt = new Appointment(
                            rs.getInt("idappt"),
                            rs.getString("title"),
                            rs.getString("type"),
                            rs.getString("descr"),
                            rs.getTimestamp("start").toLocalDateTime(),
                            rs.getTimestamp("end").toLocalDateTime(),
                            rs.getInt("clientid")
                    );
                    list.add(appt);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Deletes all appointments associated with a given client ID.
     * Must be called before marking a client inactive to maintain referential integrity.
     *
     * @param idClient ID of the client whose appointments should be deleted
     * @throws SQLException if a SQL exception occurs during deletion
     */
    public static void deleteAppointmentsByClientId(int idClient) throws SQLException {
        String sql = "DELETE FROM appt WHERE clientid = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ps.executeUpdate();
        }
    }
}
