package com.udemy.app.ws.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Disabled
class SecurityConstantsTest {

    @Test
    void testGetTokenSecret() {
        assertEquals("lk7ak456lad2jfj87fks", SecurityConstants.getTokenSecret());
    }

    @Test
    void testGetEmail() {
        assertEquals("bum6lebee@gmail.com", SecurityConstants.getEmail());
    }

    @Test
    void testGetOrigin() {
        assertEquals("http://localhost:8080", SecurityConstants.getOrigin());
    }
}