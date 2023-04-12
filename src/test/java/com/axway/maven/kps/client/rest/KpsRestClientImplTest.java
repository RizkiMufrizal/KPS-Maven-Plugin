package com.axway.maven.kps.client.rest;

import com.axway.maven.kps.client.KpsClient;
import com.axway.maven.kps.client.object.response.KPSResult;
import com.axway.maven.kps.common.Constant;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class KpsRestClientImplTest {

    @Inject
    private KpsClient kpsClient = new KpsRestClientImpl();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().httpsPort(8090))
            .build();

    @Test
    void isExistingKps() {
        wireMockExtension.stubFor(WireMock.get("/api/router/service/instance_01/api/kps/sample/12345").willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        Boolean existingKps = this.kpsClient.isExistingKps("admin", "admin", Constant.PROTOCOL + "localhost:8090" + Constant.URL_KPS("instance_01", "sample") + "/" + "12345");
        log.info("Result Is Existing Kps {}", existingKps);
        assertTrue(existingKps);
    }

    @Test
    void createKps() {
        wireMockExtension.stubFor(WireMock.put("/api/router/service/instance_01/api/kps/sample/12345").willReturn(WireMock.aResponse().withStatus(201).withResponseBody(new Body("{}"))));

        KPSResult kpsResult = this.kpsClient.createKps("admin", "admin", Constant.PROTOCOL + "localhost:8090" + Constant.URL_KPS("instance_01", "sample") + "/" + "12345", "{}");
        log.info("Result Create Kps {}", kpsResult);
        assertTrue(kpsResult.getSuccess());
    }

    @Test
    void deleteKps() {
        wireMockExtension.stubFor(WireMock.delete("/api/router/service/instance_01/api/kps/sample/12345").willReturn(WireMock.aResponse().withStatus(204).withResponseBody(new Body("{}"))));

        KPSResult kpsResult = this.kpsClient.deleteKps("admin", "admin", Constant.PROTOCOL + "localhost:8090" + Constant.URL_KPS("instance_01", "sample") + "/" + "12345");
        log.info("Result Delete Kps {}", kpsResult);
        assertTrue(kpsResult.getSuccess());
    }
}