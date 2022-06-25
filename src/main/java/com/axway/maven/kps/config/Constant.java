package com.axway.maven.kps.config;

public class Constant {
    public static final String PROTOCOL = "https://";
    public static final String URL_TOPOLOGY = "/api/topology";

    public static String URL_KPS(String instance, String tableName) {
        return "/api/router/service/" + instance + "/api/kps/" + tableName;
    }
}
