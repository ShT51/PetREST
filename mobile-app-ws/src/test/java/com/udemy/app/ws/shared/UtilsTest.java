package com.udemy.app.ws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

    @Autowired
    Utils utils;

    private final String EXPIRED_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSb0RGZDZjVE5TZldjM3IxbmFHR1VIUkZCbVFjZzYiLCJleHAiOjE1ODc1NDUwNzd9.IvQqqg_FHh2ODbfAFYSeZUluxXTVDuzf1Y-lgLD8w1zwJwS49FKfrseENnfg1m5pz7wEm2ax_ipgwEbVE_bLvg";

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGenerateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);
        assertNotNull(userId);
        assertNotNull(userId2);
        assertEquals(userId.length(), 30);
        assertFalse(userId.equalsIgnoreCase(userId2));
    }

    @Test
    void testHasTokenNotExpired() {
        String newToken = utils.generateEmailVerificationToken(utils.generateUserId(30));
        assertNotNull(newToken);
        boolean hasTokenExpired = Utils.hasTokenExpired(newToken);
        assertFalse(hasTokenExpired);
    }

    @Test
    void testHasTokenExpired() {
        boolean hasTokenExpired = Utils.hasTokenExpired(EXPIRED_TOKEN);
        assertTrue(hasTokenExpired);
    }

}