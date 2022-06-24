package com.axway.maven.kps.csv.impl;

import com.axway.maven.kps.csv.ReadCsvFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.project.MavenProject;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;

@Named
@Singleton
@Slf4j
public class ReadCsvFileImpl implements ReadCsvFile {
    @Override
    public File read(MavenProject mavenProject, String fileName) {
        String file = mavenProject.getResources().get(0).getDirectory() + File.separator + fileName;
        log.info("Read file from {}", file);
        return new File(file);
    }
}
