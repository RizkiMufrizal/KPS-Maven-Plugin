package com.axway.maven.kps;

import com.axway.maven.kps.config.Constant;
import com.axway.maven.kps.csv.Convert;
import com.axway.maven.kps.csv.ReadCsvFile;
import com.axway.maven.kps.csv.impl.ConvertJsonImpl;
import com.axway.maven.kps.csv.impl.ReadCsvFileImpl;
import com.axway.maven.kps.restclient.KpsRestClient;
import com.axway.maven.kps.restclient.TopologyRestClient;
import com.axway.maven.kps.restclient.impl.KpsRestClientImpl;
import com.axway.maven.kps.restclient.impl.TopologyRestClientImpl;
import com.axway.maven.kps.restclient.mapper.Service;
import com.axway.maven.kps.restclient.mapper.Topology;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.vandermeer.asciitable.AsciiTable;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
    private final TopologyRestClient topologyRestClient;
    private final KpsRestClient kpsRestClient;

    @Inject
    public DeployMojo(ConvertJsonImpl convertJsonImpl, ReadCsvFileImpl readCsvFileImpl, TopologyRestClientImpl topologyRestClientImpl, KpsRestClientImpl kpsRestClientImpl) {
        this.convert = convertJsonImpl;
        this.readCsvFile = readCsvFileImpl;
        this.topologyRestClient = topologyRestClientImpl;
        this.kpsRestClient = kpsRestClientImpl;
    }

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    /**
     * CSV File Name.
     *
     * @parameter
     */
    @Parameter(property = "axway.kps.csvfile", required = true)
    private String fileName;

    /**
     * KPS Table Name Alias.
     *
     * @parameter
     */
    @Parameter(property = "axway.kps.tablename", required = true)
    private String tableName;

    /**
     * API Gateway Instance Name.
     *
     * @parameter
     */
    @Parameter(property = "axway.kps.instance", required = true)
    private String instance;

    /**
     * API Gateway Manager User.
     *
     * @parameter
     */
    @Parameter(property = "axway.anm.user", required = true)
    private String username;

    /**
     * API Gateway Manager Password.
     *
     * @parameter
     */
    @Parameter(property = "axway.anm.password", required = true)
    private String password;

    /**
     * API Gateway Manager Host.
     *
     * @parameter
     */
    @Parameter(property = "axway.anm.host", required = true)
    private String host;

    /**
     * API Gateway Manager Port.
     *
     * @parameter
     */
    @Parameter(property = "axway.anm.port", required = true)
    private String port;

    /**
     * CSV Separator.
     *
     * @parameter
     */
    @Parameter(property = "axway.kps.csvseparator", defaultValue = ";")
    private Character csvSeparator;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String urlTopology = Constant.PROTOCOL + host + ":" + port + Constant.URL_TOPOLOGY;
            Topology topology = topologyRestClient.getTopologies(urlTopology);
            Optional<Service> optionalTopology = topology.getResult().getServices().stream().parallel().filter(t -> t.getName().equals(instance)).findFirst();
            if (optionalTopology.isPresent()) {

                String urlKps = Constant.PROTOCOL + host + ":" + port + Constant.URL_KPS(optionalTopology.get().getId(), tableName);
                File csvFile = readCsvFile.read(mavenProject, fileName);
                List<Map<?, ?>> csvFileListMap = convert.toMapList(csvFile, csvSeparator);

                AsciiTable asciiTable = new AsciiTable();
                asciiTable.addRow("No", "Key", "Value", "Action", "Success", "Message");

                AtomicReference<Integer> i = new AtomicReference<>(0);
                csvFileListMap.forEach(k -> {
                    i.getAndSet(i.get() + 1);
                    Object keyKps = k.keySet().stream().findFirst().orElse(null);
                    Object valueKps = k.get(keyKps);
                    Object valueAction = k.get("action");
                    Boolean isKpsExist = kpsRestClient.isExistingKps(urlKps + "/" + valueKps);
                    log.info("KPS with key {} and Value {} is {}", keyKps, valueKps, (isKpsExist ? "Exist" : "Not Exist"));
                    log.info("Action Process {}", valueAction);

                    /* create kps */
                    if (String.valueOf(valueAction).equalsIgnoreCase("INSERT")) {
                        try {
                            this.createKps(urlKps + "/" + valueKps, convert.toString(k), asciiTable, i, keyKps, valueKps, isKpsExist, valueAction);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    /* create kps */

                    /* update kps */
                    if (String.valueOf(valueAction).equalsIgnoreCase("UPDATE")) {
                        try {
                            this.updateKps(urlKps + "/" + valueKps, convert.toString(k), asciiTable, i, keyKps, valueKps, isKpsExist, valueAction);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    /* update kps */

                    /* delete kps */
                    if (String.valueOf(valueAction).equalsIgnoreCase("DELETE")) {
                        try {
                            this.deleteKps(urlKps + "/" + valueKps, asciiTable, i, keyKps, valueKps, isKpsExist, valueAction);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    /* delete kps */
                });
                log.info(asciiTable.render());
            }
            log.error("Instance Not Found");
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
    }

    /**
     * Create KPS.
     */
    private void createKps(String url, String body, AsciiTable asciiTable, AtomicReference<Integer> i, Object keyKps, Object valueKps, Boolean isKpsExist, Object valueAction) {
        if (isKpsExist) {
            log.info("Cannot Create KPS because KPS Is Exist");
            asciiTable.addRow(i.get(), keyKps, valueKps, false, "KPS Is Exist");
        } else {
            log.info("Create KPS Process");
            Map<String, Object> mapResponseKps = kpsRestClient.createKps(url, body);
            asciiTable.addRow(i.get(), keyKps, valueKps, mapResponseKps.get("Success"), mapResponseKps.get("Message"));
        }
    }

    /**
     * Update KPS.
     */
    private void updateKps(String url, String body, AsciiTable asciiTable, AtomicReference<Integer> i, Object keyKps, Object valueKps, Boolean isKpsExist, Object valueAction) {
        if (isKpsExist) {
            log.info("Delete KPS Process");
            Map<String, Object> mapResponseDeleteKps = kpsRestClient.deleteKps(url);
            if (mapResponseDeleteKps.get("Success").equals(true)) {
                log.info("Update KPS Process");
                Map<String, Object> mapResponseKps = kpsRestClient.createKps(url, body);
                asciiTable.addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"));
            } else {
                log.info("Delete KPS Process Failed");
                asciiTable.addRow(i.get(), keyKps, valueKps, valueAction + "-DELETE", mapResponseDeleteKps.get("Success"), mapResponseDeleteKps.get("Message"));
            }
        } else {
            log.info("KPS Not Exist, Create KPS Process");
            Map<String, Object> mapResponseKps = kpsRestClient.createKps(url, body);
            asciiTable.addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"));
        }
    }

    /**
     * Delete KPS.
     */
    private void deleteKps(String url, AsciiTable asciiTable, AtomicReference<Integer> i, Object keyKps, Object valueKps, Boolean isKpsExist, Object valueAction) {
        if (isKpsExist) {
            log.info("Delete KPS Process");
            Map<String, Object> mapResponseKps = kpsRestClient.deleteKps(url);
            asciiTable.addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"));
        } else {
            log.info("KPS Not Exist, Delete KPS Process Failed");
            asciiTable.addRow(i.get(), keyKps, valueKps, false, "KPS Not Exist");
        }
    }
}