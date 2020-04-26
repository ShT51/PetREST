package com.udemy.app.ws.mobileappwsassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersWebServiceEndpointsTest {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String EMAIL_ADDRESS = "night@mare.com";
    private final String JSON = "application/json";
    private static String authorization;
    private static String userId;
    private static List<Map<String, String>> addresses;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    // run this test first!
    @Test
    @Disabled
    void createUser() {
        new TestCreateUser().testCreateUser();
    }

    // authorization status should to be set to TRUE (1)
    // UPDATE Users SET Email_verification_status = 'true'
    @Test
    @Order(1)
    void testUserLogin() {
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

        authorization = response.header("Authorization");
        userId = response.header("UserID");

        assertNotNull(authorization);
        assertNotNull(userId);
        assertEquals(30, userId.length());
    }

    @Test
    @Order(2)
    void testGetUserDetails() {
        Response response = given()
                .pathParam("id", userId)
                .accept(JSON)
                .header("Authorization", authorization)
                .when()
                .get(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firsName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(lastName);
        assertNotNull(firsName);
        assertEquals(30, userPublicId.length());
        assertEquals(EMAIL_ADDRESS, userEmail);
        assertEquals(2, addresses.size());
        assertEquals(30, addressId.length());
    }

    @Test
    @Order(3)
    void testUpdateUserDetails() {
        Map<String, Object> updateDetails = new HashMap<>();
        updateDetails.put("firstName", "Fred");
        updateDetails.put("lastName", "Krug");

        Response response = given()
                .header("Authorization", authorization)
                .accept(JSON)
                .pathParam("id", userId)
                .contentType(JSON)
                .body(updateDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String firsName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

        assertEquals("Fred", firsName);
        assertEquals("Krug", lastName);
        assertNotNull(storedAddresses);
        assertEquals(addresses.size(), storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        Response response = given()
                .header("Authorization", authorization)
                .accept(JSON)
                .pathParam("id", userId)
                .when()
                .delete(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();
        String operationResult = response.jsonPath().getString("operationResult");
        String operationName = response.jsonPath().getString("operationName");
        assertEquals("SUCCESS", operationResult);
        assertEquals("DELETE", operationName);
    }
}
