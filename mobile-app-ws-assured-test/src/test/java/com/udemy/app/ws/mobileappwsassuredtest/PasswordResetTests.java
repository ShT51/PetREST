package com.udemy.app.ws.mobileappwsassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordResetTests {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String NEW_PASSWORD = "$newPassword";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    // #1. Create a new User in DB
    @Test
    @Order(1)
    void createUser() {
        new TestCreateUser().testCreateUser();
    }

    // #2 (Optional). Try to login with old password
    // Authorization status in DB should to be set to TRUE (1)
    // UPDATE Users SET Email_verification_status = 'true'
    @Test
    @Disabled
    void testUserLoginWith_Old_Password() {
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", TestHelper.USER_EMAIL);
        loginDetails.put("password", TestHelper.RAW_PASSWORD);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String authorization = response.header("Authorization");
        String userId = response.header("UserID");

        assertNotNull(authorization);
        assertNotNull(userId);
        assertEquals(30, userId.length());
    }

    // #2. Send a request for reset password
    @Test
    @Order(2)
    void testPasswordResetRequest() {
        Map<String, String> passwordResetRequestModel = new HashMap<>();
        passwordResetRequestModel.put("email", TestHelper.USER_EMAIL);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(passwordResetRequestModel)
                .when()
                .post(CONTEXT_PATH + "/users/password-reset-request")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");
        String operationName = response.jsonPath().getString("operationName");
        assertEquals("SUCCESS", operationResult);
        assertEquals("REQUEST_PASSWORD_RESET", operationName);
    }

    // #3. Password Reset Token will be stored in DB
    // SELECT * FROM PASSWORD_RESET_TOKENS
    @Test
    @Disabled
    void testPasswordReset() {
        final String  passwordResetToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJJbmREeXlyc2dJblVZVjZCVlJiQ1hjb1hSZURNaDUiLCJleHAiOjE1ODc5MzE0MTB9.LbwX5H1K9TZek5IBS5DePI3lt3WcC8BxzgAUYWllOOgxIVotQj63NICOCPP3ekVh0HS__oZWJsmPQnSAuyKmJQ";

        Map<String, String> passwordResetModel = new HashMap<>();
        passwordResetModel.put("password", NEW_PASSWORD);
        passwordResetModel.put("token", passwordResetToken);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(passwordResetModel)
                .when()
                .post(CONTEXT_PATH + "/users/password-reset")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");
        String operationName = response.jsonPath().getString("operationName");
        assertEquals("SUCCESS", operationResult);
        assertEquals("PASSWORD_RESET", operationName);
    }

    // #4. Try to login with new password
    // Authorization status in DB should to be set to TRUE (1)
    // UPDATE Users SET Email_verification_status = 'true'
    @Test
    @Disabled
    void testUserLogin() {
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", TestHelper.USER_EMAIL);
        loginDetails.put("password", NEW_PASSWORD);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String authorization = response.header("Authorization");
        String userId = response.header("UserID");

        assertNotNull(authorization);
        assertNotNull(userId);
        assertEquals(30, userId.length());
    }
}
