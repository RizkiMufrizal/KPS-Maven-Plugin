package com.axway.maven.kps.restclient.impl;

import com.axway.maven.kps.config.Constant;
import com.axway.maven.kps.config.UnirestExecution;
import com.axway.maven.kps.restclient.TopologyRestClient;
import com.axway.maven.kps.restclient.mapper.Topology;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TopologyRestClientImplTest {

    @Inject
    private TopologyRestClient topologyRestClient = new TopologyRestClientImpl();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().httpsPort(8090))
            .build();

    @Test
    void getTopologies() {
        wireMockExtension.stubFor(WireMock.get("/api/topology").willReturn(
                WireMock.aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/topology.json")));

        UnirestExecution.run("admin", "admin");
        Topology topology = this.topologyRestClient.getTopologies(Constant.PROTOCOL + "localhost:8090" + Constant.URL_TOPOLOGY);
        UnirestExecution.shutDown();
        log.info("Result topology {}", topology);
        assertNotNull(topology);
        assertFalse(topology.getResult().getServices().isEmpty());
        assertEquals(topology.getResult().getServices().get(0).getId(), "instance_01");
    }
}