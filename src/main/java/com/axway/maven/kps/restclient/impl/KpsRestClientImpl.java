package com.axway.maven.kps.restclient.impl;

import com.axway.maven.kps.restclient.KpsRestClient;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Named
@Singleton
@Slf4j
public class KpsRestClientImpl implements KpsRestClient {
    @Override
    public Boolean isExistingKps(String url) {
        HttpResponse<String> jsonNodeHttpResponse = null;
        try {
            jsonNodeHttpResponse = Unirest.get(url)
                    .header("accept", "application/json")
                    .asString();
        } catch (UnirestException e) {
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
            return jsonNodeHttpResponse.getStatus() == 200;
        }
        return false;
    }

    @Override
    public Map<String, Object> createKps(String url, String body) {
        HttpResponse<String> jsonNodeHttpResponse = null;
        try {
            jsonNodeHttpResponse = Unirest.put(url)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException e) {
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
            Map<String, Object> map = new HashMap<>();
            map.put("Success", jsonNodeHttpResponse.getStatus() == 201);
            map.put("Message", jsonNodeHttpResponse.getBody());
            return map;
        }
        return null;
    }

    @Override
    public Map<String, Object> deleteKps(String url) {
        HttpResponse<String> jsonNodeHttpResponse = null;
        try {
            jsonNodeHttpResponse = Unirest.delete(url)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .asString();
        } catch (UnirestException e) {
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
            Map<String, Object> map = new HashMap<>();
            map.put("Success", jsonNodeHttpResponse.getStatus() == 204);
            map.put("Message", jsonNodeHttpResponse.getBody());
            return map;
        }
        return null;
    }
}