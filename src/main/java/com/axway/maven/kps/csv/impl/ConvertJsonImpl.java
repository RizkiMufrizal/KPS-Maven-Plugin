package com.axway.maven.kps.csv.impl;

import com.axway.maven.kps.common.JacksonObject;
import com.axway.maven.kps.common.JacksonStringSerializer;
import com.axway.maven.kps.csv.Convert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Named
@Singleton
@Slf4j
public class ConvertJsonImpl implements Convert {
    @Override
    public String toString(Map<?, ?> data) throws JsonProcessingException {
        ObjectMapper objectMapper = JacksonObject.objectMapper();
        objectMapper.writerWithDefaultPrettyPrinter();
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new JacksonStringSerializer());
        objectMapper.registerModule(module);
        String json = objectMapper.writeValueAsString(data);
        log.info("Convert To JSON String {}", json);
        return json;
    }

    @Override
    public List<Map<?, ?>> toMapList(File file, Character separator) throws IOException {
        log.info("Convert CSV To List Map Generic");
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader().withColumnSeparator(separator);
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = csvMapper.readerFor(Map.class).with(bootstrap).readValues(file);
        return mappingIterator.readAll();
    }
}
