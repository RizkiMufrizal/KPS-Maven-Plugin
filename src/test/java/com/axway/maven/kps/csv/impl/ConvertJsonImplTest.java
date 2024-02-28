package com.axway.maven.kps.csv.impl;

import com.axway.maven.kps.csv.Convert;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
class ConvertJsonImplTest {

    @Inject
    private Convert convert = new ConvertJsonImpl();

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