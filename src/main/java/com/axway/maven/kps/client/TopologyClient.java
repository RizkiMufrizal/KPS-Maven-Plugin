package com.axway.maven.kps.client;

import com.axway.maven.kps.client.object.response.TopologyResponse;

public interface TopologyClient {
    TopologyResponse getTopologies(String username, String password, String url) throws Exception;
}
