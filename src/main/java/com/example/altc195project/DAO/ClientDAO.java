package com.example.altc195project.DAO;

import com.example.altc195project.database.JDBC;
import com.example.altc195project.models.Client;
import com.example.altc195project.models.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for performing CRUD operations on {@link Client} records.
 *
 * Provides methods to create, read, update, inactivate, and delete clients,
 * as well as lookups by ID or name and generate simple reports.
 *
 *
 * @author Jean-Pierre Atiles
 * @version 1.0
 * @since 2025-05-11
 */
public class ClientDAO {

    /**
     * Retrieves all clients from the database.
     *
     * @return an {@link ObservableList} of all {@link Client} objects
     * @throws RuntimeException if a database error occurs
     */
    public static ObservableList<Client> getAllClients() {
        ObservableList<Client> clientList = FXCollections.observableArrayList();
        String sql = "SELECT idclient, name, email, haircolor, pcode, st_pv, country, Active FROM client";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Client c = new Client(
                        rs.getInt("idclient"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("haircolor"),
                        rs.getString("pcode"),
                        rs.getString("st_pv"),
                        rs.getString("country"),
                        rs.getInt("Active")
                );
                clientList.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading all clients", e);
        }
        return clientList;
    }

    /**
     * Retrieves a single client record by its unique identifier.
     *
     * @param clientId the ID of the client to retrieve
     * @return the {@link Client} object if found, or {@code null} if no such client exists
     * @throws RuntimeException if a database error occurs
     */
    public static Client getClientById(int clientId) {
        String sql = "SELECT idclient, name, email, haircolor, pcode, st_pv, country, Active "
                + "FROM client WHERE idclient = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getInt("idclient"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("haircolor"),
                            rs.getString("pcode"),
                            rs.getString("st_pv"),
                            rs.getString("country"),
                            rs.getInt("Active")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading client by ID: " + clientId, e);
        }
        return null;
    }

    /**
     * Finds the ID of a client by their name.
     *
     * @param clientName the name of the client
     * @return the client's ID, or {@code -1} if not found
     * @throws RuntimeException if a database error occurs
     */
    public static int getClientIdByName(String clientName) {
        String sql = "SELECT idclient FROM client WHERE name = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, clientName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idclient");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up client ID for name: " + clientName, e);
        }
        return -1;
    }

    /**
     * Inserts a new client into the database.
     *
     * @param name          the client's name
     * @param email         the client's email address
     * @param hairColor     the client's hair color
     * @param postalCode    the client's postal code
     * @param stateProvince the client's state or province
     * @param country       the client's country
     * @param active        1 if active, 0 if inactive
     * @throws SQLException if a database error occurs
     */
    public static void addClient(String name,
                                 String email,
                                 String hairColor,
                                 String postalCode,
                                 String stateProvince,
                                 String country,
                                 int active) throws SQLException {
        String sql = "INSERT INTO client "
                + "(name, email, haircolor, pcode, st_pv, country, Active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hairColor);
            ps.setString(4, postalCode);
            ps.setString(5, stateProvince);
            ps.setString(6, country);
            ps.setInt(7, active);
            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing client record in the database.
     *
     * @param clientId      the ID of the client to update
     * @param name          the new name
     * @param email         the new email address
     * @param hairColor     the new hair color
     * @param postalCode    the new postal code
     * @param stateProvince the new state or province
     * @param country       the new country
     * @param active        1 to mark active, 0 to mark inactive
     * @throws SQLException if a database error occurs
     */
    public static void updateClient(int clientId,
                                    String name,
                                    String email,
                                    String hairColor,
                                    String postalCode,
                                    String stateProvince,
                                    String country,
                                    int active) throws SQLException {
        String sql = "UPDATE client SET "
                + "name = ?, email = ?, haircolor = ?, pcode = ?, "
                + "st_pv = ?, country = ?, Active = ? "
                + "WHERE idclient = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hairColor);
            ps.setString(4, postalCode);
            ps.setString(5, stateProvince);
            ps.setString(6, country);
            ps.setInt(7, active);
            ps.setInt(8, clientId);
            ps.executeUpdate();
        }
    }

    /**
     * Inactivates a client by first deleting all their appointments,
     * then setting their Active flag to 0.
     *
     * @param clientId the ID of the client to inactivate
     * @throws SQLException if a database error occurs
     */
    public static void inactivateClient(int clientId) throws SQLException {
        // Delete all appointments for this client
        ObservableList<Appointment> appts = AppointmentDAO.clientAppointments(clientId);
        appts.forEach(a -> {
            try {
                AppointmentDAO.deleteAppointment(a.getIdAppointment());
            } catch (RuntimeException ex) {
                throw new RuntimeException("Failed to delete appointment ID "
                        + a.getIdAppointment(), ex);
            }
        });

        // Mark the client inactive
        String sql = "UPDATE client SET Active = 0 WHERE idclient = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        }
    }

    /**
     * Generates a report of how many clients are active vs. inactive.
     *
     * @return an {@link ObservableList} of {@link Appointment} objects,
     *         where {@code typeAppointment} is "Active" or "Inactive"
     *         and {@code totalAppointmentTypes} is the corresponding count.
     * @throws RuntimeException if a database error occurs
     */
    public static ObservableList<Appointment> getClientActivityReport() {
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        String sql = "SELECT Active AS status, COUNT(*) AS total FROM client GROUP BY Active";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String status = rs.getInt("status") == 1 ? "Active" : "Inactive";
                int total   = rs.getInt("total");
                list.add(new Appointment(status, total));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating client activity report", e);
        }
        return list;
    }

    /**
     * Deletes a client record from the database.
     *
     * @param clientId the ID of the client to delete
     * @throws RuntimeException if a database error occurs
     */
    public static void deleteClient(int clientId) {
        String sql = "DELETE FROM client WHERE idclient = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting client ID: " + clientId, e);
        }
    }
}
