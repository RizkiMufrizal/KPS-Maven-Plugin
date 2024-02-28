package com.axway.maven.kps.csv;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Convert {

    List<Map<?, ?>> toMapList(File file, Character separator) throws IOException;
}