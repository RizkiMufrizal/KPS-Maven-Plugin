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
import com.github.freva.asciitable.AsciiTable;
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
import java.util.ArrayList;
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

                List<List<String>> asciiTable = new ArrayList<>();
                String[] headers = {"No", "Key", "Value", "Action", "Success", "Message"};

                AtomicReference<Integer> i = new AtomicReference<>(0);
                csvFileListMap.forEach(k -> {
                    log.info("====================================================================");
                    i.getAndSet(i.get() + 1);
                    Object keyKps = k.keySet().stream().findFirst().orElse(null);
                    Object valueKps = k.get(keyKps);
                    Object valueAction = k.get("action");
                    Boolean isKpsExist = kpsRestClient.isExistingKps(urlKps + "/" + valueKps);
                    log.info("KPS with Key {} and Value {} is {}", keyKps, valueKps, (isKpsExist ? "Exist" : "Not Exist"));
                    log.info("Action Process {}", valueAction);
                    k.remove("action");

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
                log.info("====================================================================");
                log.info("Result Deployment KPS");
                log.info("====================================================================");
                String[][] data = new String[asciiTable.size()][];
                for (int j = 0; j < asciiTable.size(); j++) {
                    data[j] = asciiTable.get(j).toArray(new String[0]);
                }
                System.out.println(AsciiTable.getTable(headers, data));
            } else {
                log.error("Instance Not Found");
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
    }

    /**
     * Create KPS.
     */
    private void createKps(String url, String body, List<List<String>> asciiTable, AtomicReference<Integer> i, Object keyKps, Object valueKps, Boolean isKpsExist, Object valueAction) {
        if (isKpsExist) {
            log.info("Cannot Create KPS because KPS Is Exist");
            addRow(i.get(), keyKps, valueKps, valueAction, false, "KPS Is Exist", asciiTable);
        } else {
            log.info("Create KPS Process");
            Map<String, Object> mapResponseKps = kpsRestClient.createKps(url, body);
            addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"), asciiTable);
        }
    }

    /**
     * Update KPS.
     */
    private void updateKps(String url, String body, List<List<String>> asciiTable, AtomicReference<Integer> i, Object keyKps, Object valueKps, Boolean isKpsExist, Object valueAction) {
        if (isKpsExist) {
            log.info("Delete KPS Process");
            Map<String, Object> mapResponseDeleteKps = kpsRestClient.deleteKps(url);
            if (mapResponseDeleteKps.get("Success").equals(true)) {
                log.info("Update KPS Process");
                Map<String, Object> mapResponseKps = kpsRestClient.createKps(url, body);
                addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"), asciiTable);
            } else {
                log.info("Delete KPS Process Failed");
                addRow(i.get(), keyKps, valueKps, valueAction + "-DELETE", mapResponseDeleteKps.get("Success"), mapResponseDeleteKps.get("Message"), asciiTable);
            }
        } else {
            log.info("KPS Not Exist, Create KPS Process");
            Map<String, Object> mapResponseKps = kpsRestClient.createKps(url, body);
            addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"), asciiTable);
        }
    }

    /**
     * Delete KPS.
     */
    private void deleteKps(String url, List<List<String>> asciiTable, AtomicReference<Integer> i, Object keyKps, Object valueKps, Boolean isKpsExist, Object valueAction) {
        if (isKpsExist) {
            log.info("Delete KPS Process");
            Map<String, Object> mapResponseKps = kpsRestClient.deleteKps(url);
            addRow(i.get(), keyKps, valueKps, valueAction, mapResponseKps.get("Success"), mapResponseKps.get("Message"), asciiTable);
        } else {
            log.info("KPS Not Exist, Delete KPS Process Failed");
            addRow(i.get(), keyKps, valueKps, valueAction, false, "KPS Not Exist", asciiTable);
        }
    }

    private void addRow(Integer i, Object keyKps, Object valueKps, Object valueAction, Object isKpsExist, Object message, List<List<String>> multirows) {
        List<String> rows = new ArrayList<>();
        rows.add(String.valueOf(i));
        rows.add(String.valueOf(keyKps));
        rows.add(String.valueOf(valueKps));
        rows.add(String.valueOf(valueAction));
        rows.add(String.valueOf(isKpsExist));
        rows.add(String.valueOf(message));
        multirows.add(rows);
    }
}