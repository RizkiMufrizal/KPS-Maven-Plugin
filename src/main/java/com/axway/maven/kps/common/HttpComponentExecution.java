package com.axway.maven.kps.common;

import com.axway.maven.kps.configuration.HttpComponentConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpComponentExecution extends HttpUriRequestBase {

    public HttpComponentExecution(String method, String url) {
        super(method, URI.create(url));
    }

    @SneakyThrows
    public Map<String, Object> executeJson(int connectTimeout, int activeTimeout, Object requestBody, Map<String, String> headers) {
        headers.entrySet().parallelStream().forEach(header -> this.addHeader(header.getKey(), header.getValue()));

        if (requestBody != null) {
            this.setEntity(new StringEntity(JacksonObject.objectMapper().writeValueAsString(requestBody), ContentType.APPLICATION_JSON));
        }

        return this.execute(requestBody, connectTimeout, activeTimeout);
    }

    @SneakyThrows
    private Map<String, Object> execute(Object requestBody, int connectTimeout, int activeTimeout) {
        Map<String, Object> mapLog = new HashMap<>();
        Map<String, Object> mapLogRequest = new HashMap<>();

        mapLogRequest.put("request_url", this.getUri());
        mapLogRequest.put("request_method", this.getMethod());
        mapLogRequest.put("request_header", this.getHeaders());
        mapLogRequest.put("request_body", requestBody);
        mapLog.put("request", mapLogRequest);

        try (CloseableHttpClient closeableHttpClient = HttpComponentConfiguration.config(connectTimeout, activeTimeout)) {
            long t1 = System.nanoTime();
            return closeableHttpClient.execute(this, response -> {
                long t2 = System.nanoTime();
                String responseBody = "";
                if (response.getCode() != 204) {
                    responseBody = EntityUtils.toString(response.getEntity());
                }

                Map<String, Object> mapLogResponse = new HashMap<>();
                mapLogResponse.put("response_time", this.getRequestUri() + " in " + ((t2 - t1) / 1e6d) + "ms");
                mapLogResponse.put("response_header", response.getHeaders());
                mapLogResponse.put("response_status", response.getCode());
                mapLogResponse.put("response_message", response.getReasonPhrase());
                mapLogResponse.put("response_version", response.getVersion().getMajor() + "/" + response.getVersion().getMinor());
                mapLogResponse.put("response_body", responseBody);
                mapLog.put("response", mapLogResponse);

                log.info(JacksonObject.objectMapper().writeValueAsString(mapLog));
                return mapLogResponse;
            });
        } catch (IOException e) {
            log.info(JacksonObject.objectMapper().writeValueAsString(mapLog));
            Logger.logError(e, "IOException {}");
            throw new IOException(e);
        }
    }
}
