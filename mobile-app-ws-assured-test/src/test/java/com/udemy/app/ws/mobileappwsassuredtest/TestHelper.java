package com.udemy.app.ws.mobileappwsassuredtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHelper {

    public static final String FIRST_NAME = "Freddy";
    public static final String LAST_NAME = "Krueger";
    public static final String PUBLIC_USER_ID = "$userId$";
    public static final String ENCRYPTED_PASSWORD = "$password$";
    public static final String USER_EMAIL = "night@mare.com";
    public static final String EMAIL_VERIFICATION_TOKEN = "$emailVerificationToken$";
    public static final String RAW_PASSWORD = "qwery1234";
    public static final String PUBLIC_ADDRESS_ID = "$publicAddressId$";

    public static List<Map<String, Object>> getUserAddresses() {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Springwood");
        shippingAddress.put("country", "USA");
        shippingAddress.put("streetName", "Elm Street");
        shippingAddress.put("postalCode", "666");
        shippingAddress.put("type", "shipping");
        userAddresses.add(shippingAddress);

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Springwood");
        billingAddress.put("country", "USA");
        billingAddress.put("streetName", "Elm Street");
        billingAddress.put("postalCode", "666");
        billingAddress.put("type", "billing");
        userAddresses.add(billingAddress);

        return userAddresses;
    }
}
