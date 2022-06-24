package com.axway.maven.kps.csv;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URISyntaxException;

public interface ReadCsvFile {
    File read(MavenProject mavenProject, String fileName) throws URISyntaxException;
}