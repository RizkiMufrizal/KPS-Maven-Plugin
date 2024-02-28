package com.axway.maven.kps.csv.impl;

import com.axway.maven.kps.csv.Convert;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ConvertJsonImplTest {

    @Inject
    private Convert convert = new ConvertJsonImpl();

    @Test
    void testToString() throws JsonProcessingException {
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("Message", "Success Message");
        stringObjectMap.put("Sucess", Boolean.TRUE);

        log.info("Convert Object Map {}", stringObjectMap);
        String mapToJsonString = this.convert.toString(stringObjectMap);
        assertNotNull(mapToJsonString);
        assertFalse(mapToJsonString.isEmpty());
    }

    @Test
    void toMapList() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("V1__sample__add_new_record.csv")).getFile());
        List<Map<?, ?>> mapListCsv = this.convert.toMapList(file, ';');

        log.info("Convert File CSV To Object List Map {}", mapListCsv);
        assertFalse(mapListCsv.isEmpty());
        assertEquals(mapListCsv.stream().findFirst().get().get("no"), "1");
        assertEquals(mapListCsv.stream().findFirst().get().get("name"), "sample 1");
        assertEquals(mapListCsv.stream().findFirst().get().get("action"), "INSERT");
    }
}