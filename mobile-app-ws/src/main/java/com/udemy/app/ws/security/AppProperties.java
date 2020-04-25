package com.udemy.app.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Autowired
    private Environment env;

    public String getTokenSecret() {
        return env.getProperty("tokenSecret");
    }

    public String getEmail() {
        return env.getProperty("security.constants.email");
    }

    public String getOrigin() {
        return env.getProperty("security.constants.origin");
    }
}
