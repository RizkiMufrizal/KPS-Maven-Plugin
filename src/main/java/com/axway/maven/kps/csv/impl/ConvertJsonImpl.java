package com.axway.maven.kps.csv.impl;

import com.axway.maven.kps.csv.Convert;
import com.fasterxml.jackson.databind.MappingIterator;
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
    public List<Map<?, ?>> toMapList(File file, Character separator) throws IOException {
        log.info("Convert CSV To List Map Generic");
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader().withColumnSeparator(separator);
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = csvMapper.readerFor(Map.class).with(bootstrap).readValues(file);
        return mappingIterator.readAll();
    }
}
