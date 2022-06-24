package com.axway.maven.kps.csv;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Convert {
    String toString(Map<?, ?> data) throws JsonProcessingException;

    List<Map<?, ?>> toMapList(File file, Character separator) throws IOException;
}