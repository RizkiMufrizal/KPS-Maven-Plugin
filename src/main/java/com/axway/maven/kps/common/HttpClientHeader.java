package com.axway.maven.kps.common;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HttpClientHeader {
    public static Map<String, String> setAuthorization(String username, String password) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        return headers;
    }
}
