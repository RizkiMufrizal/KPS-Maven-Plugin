package com.axway.maven.kps.client;

import com.axway.maven.kps.client.object.response.KPSResult;

public interface KpsClient {
    Boolean isExistingKps(String username, String password, String url);

    KPSResult createKps(String username, String password, String url, String body);

    KPSResult deleteKps(String username, String password, String url);
}