package com.udemy.app.ws.mobileappwsassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;
import static org.junit.jupiter.api.Assertions.*;

public class TestCreateUser {

    private final String CONTEXT_PATH = "/mobile-app-ws";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testCreateUser() {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Springwood");
        shippingAddress.put("country", "USA");
        shippingAddress.put("streetName", "Elm Street");
        shippingAddress.put("postalCode", "666");
        shippingAddress.put("type", "shipping");

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Springwood");
        billingAddress.put("country", "USA");
        billingAddress.put("streetName", "Elm Street");
        billingAddress.put("postalCode", "666");
        billingAddress.put("type", "billing");

        userAddresses.add(shippingAddress);
        userAddresses.add(billingAddress);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Freddy");
        userDetails.put("lastName", "Krueger");
        userDetails.put("email", "night@mare.com");
        userDetails.put("password", "1234");
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
