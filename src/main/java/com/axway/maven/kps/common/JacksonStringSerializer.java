package com.axway.maven.kps.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JacksonStringSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (s.equalsIgnoreCase("true")) {
            jsonGenerator.writeBoolean(true);
        } else if (s.equalsIgnoreCase("false")) {
            jsonGenerator.writeBoolean(false);
        } else {
            jsonGenerator.writeString(s);
        }
    }
}
