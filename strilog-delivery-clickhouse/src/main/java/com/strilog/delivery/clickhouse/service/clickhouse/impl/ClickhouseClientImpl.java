package com.strilog.delivery.clickhouse.service.clickhouse.impl;

import com.strilog.delivery.clickhouse.service.clickhouse.IClickhouseClient;
import com.strilog.delivery.clickhouse.service.clickhouse.model.ClickhouseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClickhouseClientImpl implements IClickhouseClient {

    private static final Logger LOG = LoggerFactory.getLogger(ClickhouseClientImpl.class);

    private final HttpClient httpClient;
    private final URI        url;
    private final String     authHeader;

    public ClickhouseClientImpl(ClickhouseConnection aConnection) {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        url        = createUrl(aConnection);
        authHeader = createAuthHeader(aConnection.getUsername(), aConnection.getPassword());

        LOG.trace("Base url is {}", url);
    }

    private String createAuthHeader(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(UTF_8));
    }

    private static URI createUrl(ClickhouseConnection aConnection) {
        String baseUrl = "http://" + aConnection.getHostname() + ":" + aConnection.getPort() + "/";
        return new UriBuilder(baseUrl)
                .param("input_format_import_nested_json", "1")
                .param("send_progress_in_http_headers", "1")
                .param("query", "insert into " + aConnection.getTable() + " FORMAT JSONEachRow")
                .toUri();
    }

    @Override
    public void insertFile(File aFile) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(createBodyPublisher(aFile))
                .header("authorization", authHeader)
                .timeout(Duration.ofMinutes(1))
                .build();

        try {
            LOG.debug("Inserting file {} to {}", aFile.getAbsolutePath(), url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Cannot insert file " + aFile.getAbsolutePath() + " : " + response.body());
            }
            LOG.debug("Inserted file {} : {}", aFile.getAbsolutePath(), response.body());
            dumpHeaders(response);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot insert file " + aFile.getAbsolutePath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Cannot insert file " + aFile.getAbsolutePath(), e);
        }
    }

    private void dumpHeaders(HttpResponse<String> response) {
        LOG.debug("Headers");
        for (Map.Entry<String, List<String>> entry : response.headers().map().entrySet()) {
            if(entry.getValue().size() == 1) {
                LOG.debug("  header {} = {}", entry.getKey(), entry.getValue().getFirst());
            } else {
                LOG.debug("  header {}", entry.getKey());
                for (String value : entry.getValue()) {
                    LOG.debug("    value {}", value);
                }
            }
        }
    }

    private static HttpRequest.BodyPublisher createBodyPublisher(File aFile) {
        try {
            return HttpRequest.BodyPublishers.ofFile(aFile.toPath());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot find file " + aFile.getAbsolutePath(), e);
        }
    }
}
