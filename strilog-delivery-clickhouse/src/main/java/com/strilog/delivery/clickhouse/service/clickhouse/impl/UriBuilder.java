package com.strilog.delivery.clickhouse.service.clickhouse.impl;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UriBuilder {

    private final String        baseUrl;
    private final StringBuilder sb = new StringBuilder();
    private boolean firstAdded;

    public UriBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
        sb.append(baseUrl);
        firstAdded = baseUrl.contains("?");
    }

    public UriBuilder param(String aName, int aValue) {
        return param(aName, String.valueOf(aValue));
    }

    public UriBuilder param(String aName, long aValue) {
        return param(aName, String.valueOf(aValue));
    }

    public UriBuilder param(String aName, String aValue) {
        if (firstAdded) {
            sb.append("&");
        } else {
            firstAdded = true;
            sb.append("?");
        }

        sb.append(percentEncode(aName, UTF_8));
        sb.append("=");
        sb.append(percentEncode(aValue, UTF_8));

        return this;
    }

    @SneakyThrows
    public URI toUri() {
        return new URI(sb.toString());
    }

    public static String percentEncode(String str, Charset charset) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return URLEncoder.encode(str, charset)
//                .replace("+", "%20")
//                .replace("*", "%2A")
//                .replace("%7E", "~")
                ;
    }

}
