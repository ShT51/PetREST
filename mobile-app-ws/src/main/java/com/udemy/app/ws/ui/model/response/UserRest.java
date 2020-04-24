package com.udemy.app.ws.ui.model.response;

import java.util.List;

/**
 * Using this class to Response for HTTP requests
 * we can control which data we will send back to the client.
 * For example, we don't send back the Password
 * and the User's ID is not the same as in the Data base, it will be AlphaNumeric value
 */
public class UserRest {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<AddressRest> addresses;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AddressRest> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressRest> addresses) {
        this.addresses = addresses;
    }
}
