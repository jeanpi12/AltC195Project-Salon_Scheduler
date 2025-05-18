package com.example.altc195project.models;

/**
 * Represents a salon client, storing personal and contact details,
 * as well as their activity status.
 */
public class Client {
    private int clientId;
    private String clientName;
    private String clientEmail;
    private String clientHairColor;
    private String clientPostalCode;
    private String clientState;
    private String clientCountry;
    private int clientActivity;

    /**
     * Constructs a new Client with the specified details.
     *
     * @param clientId         unique identifier for the client
     * @param clientName       full name of the client
     * @param clientEmail      email address of the client
     * @param clientHairColor  hair color description of the client
     * @param clientPostalCode postal code for the client's address (as String)
     * @param clientState      state or province of the client's address
     * @param clientCountry    country of the client's address
     * @param clientActivity   activity status flag (1 = active, 0 = inactive)
     */
    public Client(int clientId,
                  String clientName,
                  String clientEmail,
                  String clientHairColor,
                  String clientPostalCode,
                  String clientState,
                  String clientCountry,
                  int clientActivity)
    {
        this.clientId         = clientId;
        this.clientName       = clientName;
        this.clientEmail      = clientEmail;
        this.clientHairColor  = clientHairColor;
        this.clientPostalCode = clientPostalCode;
        this.clientState      = clientState;
        this.clientCountry    = clientCountry;
        this.clientActivity   = clientActivity;
    }

    /**
     * Retrieves the unique client ID.
     *
     * @return the client's ID
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Sets the unique client ID.
     *
     * @param clientId the ID to assign to the client
     */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Retrieves the client's full name.
     *
     * @return the client's name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the client's full name.
     *
     * @param clientName the name to assign to the client
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Retrieves the client's email address.
     *
     * @return the client's email
     */
    public String getClientEmail() {
        return clientEmail;
    }

    /**
     * Sets the client's email address.
     *
     * @param clientEmail the email to assign to the client
     */
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    /**
     * Retrieves the client's hair color description.
     *
     * @return the client's hair color
     */
    public String getClientHairColor() {
        return clientHairColor;
    }

    /**
     * Sets the client's hair color description.
     *
     * @param clientHairColor the hair color to assign to the client
     */
    public void setClientHairColor(String clientHairColor) {
        this.clientHairColor = clientHairColor;
    }

    /**
     * Retrieves the client's postal code.
     *
     * @return the client's postal code
     */
    public String getClientPostalCode() {
        return clientPostalCode;
    }

    /**
     * Sets the client's postal code.
     *
     * @param clientPostalCode the postal code to assign to the client
     */
    public void setClientPostalCode(String clientPostalCode) {
        this.clientPostalCode = clientPostalCode;
    }

    /**
     * Retrieves the client's state or province.
     *
     * @return the client's state
     */
    public String getClientState() {
        return clientState;
    }

    /**
     * Sets the client's state or province.
     *
     * @param clientState the state to assign to the client
     */
    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    /**
     * Retrieves the client's country.
     *
     * @return the client's country
     */
    public String getClientCountry() {
        return clientCountry;
    }

    /**
     * Sets the client's country.
     *
     * @param clientCountry the country to assign to the client
     */
    public void setClientCountry(String clientCountry) {
        this.clientCountry = clientCountry;
    }

    /**
     * Retrieves the client's activity status.
     *
     * @return 1 if active, 0 if inactive
     */
    public int getClientActivity() {
        return clientActivity;
    }

    /**
     * Sets the client's activity status.
     *
     * @param clientActivity 1 to mark active, 0 to mark inactive
     */
    public void setClientActivity(int clientActivity) {
        this.clientActivity = clientActivity;
    }
}
