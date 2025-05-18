package com.example.altc195project.DAO;

import com.example.altc195project.database.JDBC;
import com.example.altc195project.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides data-access operations for the {@code user} table.
 *
 * All methods use the shared {@link JDBC#connection} to perform queries
 * and updates against the database.
 *
 *
 * @author Jean-Pierre Atiles
 * @version 1.0
 */
public class UserDAO {

    /**
     * Retrieves all users from the database.
     *
     * @return an {@link ObservableList} of {@link User} objects,
     *         each containing the user's ID and login name.
     * @throws RuntimeException if a database access error occurs.
     */
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT idUser, loginName FROM user";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("idUser"),
                        rs.getString("loginName")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all users", e);
        }
        return users;
    }

    /**
     * Validates a login attempt by checking that the given credentials
     * match a user record in the database.
     *
     * @param loginName the login name to validate
     * @param password  the password to validate
     * @return {@code true} if a matching record exists; {@code false} otherwise
     * @throws RuntimeException if a database access error occurs.
     */
    public static boolean validateCredentials(String loginName, String password) {
        String sql = "SELECT 1 FROM user WHERE loginName = ? AND psw = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, loginName);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to validate credentials for user: " + loginName, e);
        }
    }

    /**
     * Checks whether a login name exists in the database (case-sensitive).
     *
     * @param loginName the login name to check
     * @return {@code true} if the login name exists exactly as provided;
     *         {@code false} otherwise
     * @throws RuntimeException if a database access error occurs.
     */
    public static boolean isLoginNameValid(String loginName) {
        String sql = "SELECT 1 FROM user WHERE BINARY loginName = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, loginName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence of login name: " + loginName, e);
        }
    }

    /**
     * Retrieves a full {@link User} record, including password, by login name.
     *
     * @param loginName the login name to look up
     * @return a {@code User} object if found; {@code null} otherwise
     * @throws RuntimeException if a database access error occurs.
     */
    public static User getUserByLoginName(String loginName) {
        String sql = "SELECT idUser, loginName, psw FROM user WHERE loginName = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, loginName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("idUser"),
                            rs.getString("loginName"),
                            rs.getString("psw")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user by login name: " + loginName, e);
        }
        return null;
    }

    /**
     * Retrieves a {@link User} record by its unique ID.
     *
     * @param idUser the unique ID of the user
     * @return a {@code User} object if found; {@code null} otherwise
     * @throws RuntimeException if a database access error occurs.
     */
    public static User getUserById(int idUser) {
        String sql = "SELECT idUser, loginName, psw FROM user WHERE idUser = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("idUser"),
                            rs.getString("loginName"),
                            rs.getString("psw")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user by ID: " + idUser, e);
        }
        return null;
    }

    /**
     * Looks up a user's ID by their login name.
     *
     * @param loginName the login name of the user
     * @return the user's ID if found; {@code -1} if no matching user exists
     * @throws RuntimeException if a database access error occurs.
     */
    public static int getUserIdByLoginName(String loginName) {
        String sql = "SELECT idUser FROM user WHERE loginName = ?";
        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            ps.setString(1, loginName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idUser");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up user ID for login name: " + loginName, e);
        }
        return -1;
    }
}
