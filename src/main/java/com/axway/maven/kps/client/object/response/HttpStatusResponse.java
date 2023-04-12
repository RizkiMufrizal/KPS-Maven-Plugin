package com.axway.maven.kps.client.object.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HttpStatusResponse implements Serializable {
    @JsonProperty("httpCode")
    private Integer httpCode;
    @JsonProperty("httpMessage")
    private String httpMessage;
}
