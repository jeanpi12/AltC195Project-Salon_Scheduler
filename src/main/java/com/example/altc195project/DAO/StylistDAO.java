package com.example.altc195project.DAO;

import com.example.altc195project.database.JDBC;
import com.example.altc195project.models.Stylist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@code StylistDAO} is the Data Access Object responsible for
 * all database operations related to {@link Stylist} objects.
 *
 * It uses JDBC to connect to a MySQL database and provides methods
 * to fetch all stylists, lookup a stylist by ID, and find a stylist's ID by name.
 *
 */
public class StylistDAO {

    /**
     * Retrieves all stylists from the database.
     *
     * @return an {@code ObservableList<Stylist>} containing every stylist record in the database
     * @throws RuntimeException if a database access error occurs
     */
    public static ObservableList<Stylist> getAllStylists() {
        ObservableList<Stylist> stylistList = FXCollections.observableArrayList();
        String sql = "SELECT idstylist, name FROM stylist";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stylistList.add(new Stylist(
                        rs.getInt("idstylist"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stylists", e);
        }
        return stylistList;
    }

    /**
     * Finds and returns a {@link Stylist} by their unique ID.
     *
     * @param stylistId the unique identifier of the stylist
     * @return a {@code Stylist} object if found; {@code null} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public static Stylist getStylistById(int stylistId) {
        String sql = "SELECT idstylist, name FROM stylist WHERE idstylist = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, stylistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Stylist(
                            rs.getInt("idstylist"),
                            rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up stylist by ID: " + stylistId, e);
        }
        return null;
    }

    /**
     * Looks up a stylist's database ID by their exact name.
     *
     * @param stylistName the full name of the stylist
     * @return the stylist's ID if found; {@code -1} if no matching stylist exists
     * @throws RuntimeException if a database access error occurs
     */
    public static int getStylistIdByName(String stylistName) {
        String sql = "SELECT idstylist FROM stylist WHERE name = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, stylistName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idstylist");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up stylist ID by name: " + stylistName, e);
        }
        return -1;
    }
}
