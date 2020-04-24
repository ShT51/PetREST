package com.udemy.app.ws.ui.model.request;

/**
 * Use this class to convert incoming JSON payload from Login requests into POJO
 */
public class UserLoginRequestModel {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
