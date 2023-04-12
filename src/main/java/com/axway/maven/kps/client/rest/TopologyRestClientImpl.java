package com.axway.maven.kps.client.rest;

import com.axway.maven.kps.client.TopologyClient;
import com.axway.maven.kps.client.object.response.TopologyResponse;
import com.axway.maven.kps.common.CastingClass;
import com.axway.maven.kps.common.HttpClientHeader;
import com.axway.maven.kps.common.HttpComponentExecution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

@Named
@Singleton
@Slf4j
public class TopologyRestClientImpl implements TopologyClient {

    @SneakyThrows
    @Override
    public TopologyResponse getTopologies(String username, String password, String url) {
        int connectTimeout = 30000;
        int socketTimeout = 30000;
        Map<String, String> headers = HttpClientHeader.setAuthorization(username, password);

        HttpComponentExecution httpComponentExecution = new HttpComponentExecution("GET", url);
        Map<String, Object> mapResponse = httpComponentExecution.executeJson(connectTimeout, socketTimeout, null, headers);
        return CastingClass.convertInstanceOfObjectHttpClient(mapResponse, TopologyResponse.class);
    }
}