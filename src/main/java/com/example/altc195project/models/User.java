package com.example.altc195project.models;

/**
 * Model representing an application user.
 */
public class User {
    private int idUser;
    private String loginName;
    private String password;

    /**
     * Full constructor.
     * @param idUser     unique user ID
     * @param loginName  the login name
     * @param password   the user's password
     */
    public User(int idUser, String loginName, String password) {
        this.idUser = idUser;
        this.loginName = loginName;
        this.password = password;
    }

    /**
     * Constructor without password (e.g. when listing users).
     */
    public User(int idUser, String loginName) {
        this(idUser, loginName, null);
    }

    /**
     * Minimal constructor (e.g. when only loginName is known).
     */
    public User(String loginName) {
        this(0, loginName, null);
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
