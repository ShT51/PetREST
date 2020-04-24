package com.udemy.app.ws.security;

import com.udemy.app.ws.SpringApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000*60*60; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String SIGN_UP_URL = "/users";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";
    public static final String H2_CONSOLE = "/h2-console/**";

    // return the token secret key from the property file
    public static String getTokenSecret() {
        AppProperties appProperties = SpringApplicationContext.getBean("appProperties", AppProperties.class);
        return appProperties.getTokenSecret();
    }

}
