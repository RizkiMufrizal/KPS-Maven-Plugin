package com.axway.maven.kps.client.rest;

import com.axway.maven.kps.client.TopologyClient;
import com.axway.maven.kps.client.object.response.TopologyResponse;
import com.axway.maven.kps.common.Constant;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class TopologyResponseRestClientImplTest {

    @Inject
    private TopologyClient topologyClient = new TopologyRestClientImpl();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().httpsPort(8090))
            .build();

    @SneakyThrows
    @Test
    void getTopologies() {
        wireMockExtension.stubFor(WireMock.get("/api/topology").willReturn(
                WireMock.aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/topology.json")));

        TopologyResponse topologyResponse = this.topologyClient.getTopologies("admin", "admin", Constant.PROTOCOL + "localhost:8090" + Constant.URL_TOPOLOGY);
        log.info("Result topology {}", topologyResponse);
        assertNotNull(topologyResponse);
        assertFalse(topologyResponse.getResultResponse().getServiceResponses().isEmpty());
        assertEquals(topologyResponse.getResultResponse().getServiceResponses().get(0).getId(), "instance_01");
    }
}