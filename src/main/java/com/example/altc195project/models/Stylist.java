package com.example.altc195project.models;

/**
 * Represents a stylist in the salon scheduling application.
 *
 * Holds the unique identifier and name of a stylist.
 */
public class Stylist {
    /** The unique identifier for the stylist. */
    private int stylistId;
    /** The full name of the stylist. */
    private String stylistName;

    /**
     * Creates a new Stylist instance with the given ID and name.
     *
     * @param stylistId   the unique ID of the stylist
     * @param stylistName the name of the stylist
     */
    public Stylist(int stylistId, String stylistName) {
        this.stylistId = stylistId;
        this.stylistName = stylistName;
    }

    /**
     * Returns the stylist's unique identifier.
     *
     * @return the stylistId
     */
    public int getStylistId() {
        return stylistId;
    }

    /**
     * Updates the stylist's unique identifier.
     *
     * @param stylistId the new stylistId to set
     */
    public void setStylistId(int stylistId) {
        this.stylistId = stylistId;
    }

    /**
     * Returns the stylist's name.
     *
     * @return the stylistName
     */
    public String getStylistName() {
        return stylistName;
    }

    /**
     * Updates the stylist's name.
     *
     * @param stylistName the new name to set
     */
    public void setStylistName(String stylistName) {
        this.stylistName = stylistName;
    }

    /**
     * Returns the string representation of the stylist, which is the stylist's name.
     *
     * @return the stylistName
     */
    @Override
    public String toString() {
        return stylistName;
    }
}
