package com.axway.maven.kps;

import com.axway.maven.kps.csv.Convert;
import com.axway.maven.kps.csv.ReadCsvFile;
import com.axway.maven.kps.csv.impl.ConvertJsonImpl;
import com.axway.maven.kps.csv.impl.ReadCsvFileImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Deployment KPS to Spesific Instance and Table with CSV Format.
 *
 * @phase compile
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.NONE)
@Slf4j
public class DeployMojo extends AbstractMojo {

    private final Convert convert;
    private final ReadCsvFile readCsvFile;

    @Inject
    public DeployMojo(ConvertJsonImpl convertJsonImpl, ReadCsvFileImpl readCsvFileImpl) {
        this.convert = convertJsonImpl;
        this.readCsvFile = readCsvFileImpl;
    }

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    /**
     * CSV File Name.
     * @parameter
     */
    @Parameter(property = "axway.kps.csvfile", required = true)
    private String fileName;

    /**
     * KPS Table Name Alias.
     * @parameter
     */
    @Parameter(property = "axway.kps.tablename", required = true)
    private String tableName;

    /**
     * API Gateway Instance Name.
     * @parameter
     */
    @Parameter(property = "axway.kps.instance", required = true)
    private String instance;

    /**
     * API Gateway Manager User.
     * @parameter
     */
    @Parameter(property = "axway.anm.user", required = true)
    private String username;

    /**
     * API Gateway Manager Password.
     * @parameter
     */
    @Parameter(property = "axway.anm.password", required = true)
    private String password;

    /**
     * API Gateway Manager Host.
     * @parameter
     */
    @Parameter(property = "axway.anm.host", required = true)
    private String host;

    /**
     * API Gateway Manager Port.
     * @parameter
     */
    @Parameter(property = "axway.anm.port", required = true)
    private String port;

    /**
     * CSV Separator.
     * @parameter
     */
    @Parameter(property = "axway.kps.csvseparator", defaultValue = ";")
    private Character csvSeparator;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File csvFile = readCsvFile.read(mavenProject, fileName);
            List<Map<?, ?>> csvFileListMap = convert.toMapList(csvFile, csvSeparator);
            csvFileListMap.forEach(c -> {
                try {
                    convert.toString(c);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
    }
}