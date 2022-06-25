package com.axway.maven.kps.restclient.impl;

import com.axway.maven.kps.restclient.TopologyRestClient;
import com.axway.maven.kps.restclient.mapper.Topology;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Named
@Singleton
@Slf4j
public class TopologyRestClientImpl implements TopologyRestClient {
    @Override
    public Topology getTopologies(String url) {
        HttpResponse<Topology> jsonNodeHttpResponse = null;
        try {
            jsonNodeHttpResponse = Unirest.get(url).asObject(Topology.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }

        if (jsonNodeHttpResponse != null) {
            log.info("url : {}", url);
            log.debug("Header : {}", jsonNodeHttpResponse.getHeaders());
            log.info("Body : {}", jsonNodeHttpResponse.getBody());
            log.info("Http Response : {}", jsonNodeHttpResponse.getStatus());
        }

        if (jsonNodeHttpResponse != null) {
            return jsonNodeHttpResponse.getBody();
        }
        return null;
    }
}