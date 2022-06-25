package com.axway.maven.kps.restclient;

import com.axway.maven.kps.restclient.mapper.Topology;

public interface TopologyRestClient {
    Topology getTopologies(String url);
}
