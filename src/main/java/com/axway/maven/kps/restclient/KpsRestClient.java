package com.axway.maven.kps.restclient;

import java.util.Map;

public interface KpsRestClient {
    Boolean isExistingKps(String url);

    Map<String, Object> createKps(String url, String body);

    Map<String, Object> deleteKps(String url);
}