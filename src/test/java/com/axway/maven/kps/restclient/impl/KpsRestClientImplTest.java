package com.axway.maven.kps.restclient.impl;

import com.axway.maven.kps.config.Constant;
import com.axway.maven.kps.config.UnirestExecution;
import com.axway.maven.kps.restclient.KpsRestClient;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class KpsRestClientImplTest {

    @Inject
    private KpsRestClient kpsRestClient = new KpsRestClientImpl();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().httpsPort(8090))
            .build();

    @Test
    void isExistingKps() {
        wireMockExtension.stubFor(WireMock.get("/api/router/service/instance_01/api/kps/sample/12345").willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        UnirestExecution.run("admin", "admin");
        Boolean existingKps = this.kpsRestClient.isExistingKps(Constant.PROTOCOL + "localhost:8090" + Constant.URL_KPS("instance_01", "sample") + "/" + "12345");
        UnirestExecution.shutDown();
        log.info("Result Is Existing Kps {}", existingKps);
        assertTrue(existingKps);
    }

    @Test
    void createKps() {
        wireMockExtension.stubFor(WireMock.put("/api/router/service/instance_01/api/kps/sample/12345").willReturn(WireMock.aResponse().withStatus(201).withResponseBody(new Body("{}"))));

        UnirestExecution.run("admin", "admin");
        Map<String, Object> stringObjectMap = this.kpsRestClient.createKps(Constant.PROTOCOL + "localhost:8090" + Constant.URL_KPS("instance_01", "sample") + "/" + "12345", "{}");
        UnirestExecution.shutDown();
        log.info("Result Create Kps {}", stringObjectMap);
        assertTrue((Boolean) stringObjectMap.get("Success"));
        assertEquals(stringObjectMap.get("Success"), Boolean.TRUE);
    }

    @Test
    void deleteKps() {
        wireMockExtension.stubFor(WireMock.delete("/api/router/service/instance_01/api/kps/sample/12345").willReturn(WireMock.aResponse().withStatus(204).withResponseBody(new Body("{}"))));

        UnirestExecution.run("admin", "admin");
        Map<String, Object> stringObjectMap = this.kpsRestClient.deleteKps(Constant.PROTOCOL + "localhost:8090" + Constant.URL_KPS("instance_01", "sample") + "/" + "12345");
        UnirestExecution.shutDown();
        log.info("Result Delete Kps {}", stringObjectMap);
        assertTrue((Boolean) stringObjectMap.get("Success"));
        assertEquals(stringObjectMap.get("Success"), Boolean.TRUE);
    }
}