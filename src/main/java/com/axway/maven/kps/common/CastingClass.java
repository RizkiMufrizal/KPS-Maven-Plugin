package com.axway.maven.kps.common;

import com.axway.maven.kps.client.object.response.GeneralStringResponse;
import lombok.SneakyThrows;

import java.util.Map;

public class CastingClass {
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T convertInstanceOfObjectHttpClient(Map<String, Object> map, Class<T> clazz) {
        Map<String, Object> objectMap = JacksonObject.objectMapper().readValue(String.valueOf(map.get("response_body")), Map.class);
        objectMap.put("httpCode", map.get("response_status"));
        objectMap.put("httpMessage", map.get("response_message"));
        return JacksonObject.objectMapper().convertValue(objectMap, clazz);
    }

    @SneakyThrows
    public static GeneralStringResponse convertInstanceOfObjectHttpClient(Map<String, Object> map) {
        GeneralStringResponse generalStringResponse = new GeneralStringResponse();
        generalStringResponse.setResponse(String.valueOf(map.get("response_body")));
        generalStringResponse.setHttpCode(Integer.valueOf(String.valueOf(map.get("response_status"))));
        generalStringResponse.setHttpMessage(String.valueOf(map.get("response_message")));
        return generalStringResponse;
    }
}