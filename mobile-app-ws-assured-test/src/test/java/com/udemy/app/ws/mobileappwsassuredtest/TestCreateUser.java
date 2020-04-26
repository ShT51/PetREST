package com.udemy.app.ws.mobileappwsassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestCreateUser {

    private final String CONTEXT_PATH = "/mobile-app-ws";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testCreateUser() {
        List<Map<String, Object>> userAddresses = TestHelper.getUserAddresses();

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", TestHelper.FIRST_NAME);
        userDetails.put("lastName", TestHelper.LAST_NAME);
        userDetails.put("email", TestHelper.USER_EMAIL);
        userDetails.put("password", TestHelper.RAW_PASSWORD);
        userDetails.put("addresses", userAddresses);


        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");
        assertNotNull(userId);
        assertEquals(30, userId.length());

        String responseBody = response.body().asString();
        try {
            JSONObject responseBodyJson = new JSONObject(responseBody);
            JSONArray addresses = responseBodyJson.getJSONArray("addresses");

            assertNotNull(addresses);
            assertEquals(2, addresses.length());

            String addressId = addresses.getJSONObject(0).getString("addressId");
            assertNotNull(addressId);
            assertEquals(30, addressId.length());
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }
}
