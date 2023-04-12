package com.axway.maven.kps.client.object.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneralStringResponse extends HttpStatusResponse implements Serializable {
    private String response;
}