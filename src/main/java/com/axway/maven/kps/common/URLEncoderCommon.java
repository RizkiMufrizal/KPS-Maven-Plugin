package com.axway.maven.kps.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class URLEncoderCommon {

    public static String toEncodeString(Object value) throws UnsupportedEncodingException {
        return URLEncoder.encode(String.valueOf(value), Charset.defaultCharset()).replace("+", "%20");
    }
}
