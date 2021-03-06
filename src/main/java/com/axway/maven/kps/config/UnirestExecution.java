package com.axway.maven.kps.config;

import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class UnirestExecution {
    public static void run(String username, String password) {
        log.info("Run Unirest Config");
        StringWriter stringWriter = new StringWriter();
        Unirest.config()
                .clearDefaultHeaders()
                .setObjectMapper(new ObjectMapper() {
                    private final com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                            = new com.fasterxml.jackson.databind.ObjectMapper();

                    public <T> T readValue(String value, Class<T> valueType) {
                        try {
                            return jacksonObjectMapper.readValue(value, valueType);
                        } catch (Exception e) {
                            log.error("Exception {}", e.getMessage());
                            log.error("Exception {}", new PrintWriter(stringWriter));
                            return null;
                        }
                    }

                    public String writeValue(Object value) {
                        try {
                            return jacksonObjectMapper.writeValueAsString(value);
                        } catch (Exception e) {
                            log.error("Exception {}", e.getMessage());
                            log.error("Exception {}", new PrintWriter(stringWriter));
                            return null;
                        }
                    }
                })
                .socketTimeout(30000)
                .connectTimeout(30000)
                .verifySsl(false);
        if (!username.isEmpty() && !password.isEmpty()) {
            Unirest.config().setDefaultBasicAuth(username, password);
        }
    }

    public static void shutDown() {
        log.info("Unirest Shutdown");
        Unirest.shutDown();
    }
}
