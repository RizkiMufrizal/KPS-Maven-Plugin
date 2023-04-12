package com.axway.maven.kps.common;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class Logger {
    public static void logError(Exception e, String message) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        log.error(message, stringWriter);
    }

    public static void logError(Throwable e, String message) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        log.error(message, stringWriter);
    }
}
