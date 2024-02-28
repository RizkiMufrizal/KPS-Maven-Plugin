package com.axway.maven.kps.client.rest;

import com.axway.maven.kps.client.KpsClient;
import com.axway.maven.kps.client.object.response.GeneralStringResponse;
import com.axway.maven.kps.client.object.response.KPSResult;
import com.axway.maven.kps.common.CastingClass;
import com.axway.maven.kps.common.HttpClientHeader;
import com.axway.maven.kps.common.HttpComponentExecution;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

@Named
@Singleton
@Slf4j
public class KpsRestClientImpl implements KpsClient {
    @Override
    public Boolean isExistingKps(String username, String password, String url) {
        int connectTimeout = 30000;
        int socketTimeout = 30000;
        Map<String, String> headers = HttpClientHeader.setAuthorization(username, password);

        HttpComponentExecution httpComponentExecution = new HttpComponentExecution("GET", url);
        Map<String, Object> mapResponse = httpComponentExecution.executeJson(connectTimeout, socketTimeout, null, headers);
        GeneralStringResponse generalStringResponse = CastingClass.convertInstanceOfObjectHttpClient(mapResponse);

        if (generalStringResponse.getHttpCode() != null) {
            return generalStringResponse.getHttpCode() == 200;
        }
        return false;
    }

    @Override
    public KPSResult createKps(String username, String password, String url, Object body) {
        int connectTimeout = 30000;
        int socketTimeout = 30000;
        Map<String, String> headers = HttpClientHeader.setAuthorization(username, password);

        HttpComponentExecution httpComponentExecution = new HttpComponentExecution("PUT", url);
        Map<String, Object> mapResponse = httpComponentExecution.executeJson(connectTimeout, socketTimeout, body, headers);
        GeneralStringResponse generalStringResponse = CastingClass.convertInstanceOfObjectHttpClient(mapResponse);

        return KPSResult.builder()
                .message(generalStringResponse.getResponse())
                .success(generalStringResponse.getHttpCode() == 201)
                .build();
    }

    @Override
    public KPSResult deleteKps(String username, String password, String url) {
        int connectTimeout = 30000;
        int socketTimeout = 30000;
        Map<String, String> headers = HttpClientHeader.setAuthorization(username, password);

        HttpComponentExecution httpComponentExecution = new HttpComponentExecution("DELETE", url);
        Map<String, Object> mapResponse = httpComponentExecution.executeJson(connectTimeout, socketTimeout, null, headers);
        GeneralStringResponse generalStringResponse = CastingClass.convertInstanceOfObjectHttpClient(mapResponse);

        return KPSResult.builder()
                .message(generalStringResponse.getResponse())
                .success(generalStringResponse.getHttpCode() == 204)
                .build();
    }
}