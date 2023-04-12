package com.axway.maven.kps.client.object.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class KPSResult implements Serializable {
    private Boolean success;
    private String message;
}
