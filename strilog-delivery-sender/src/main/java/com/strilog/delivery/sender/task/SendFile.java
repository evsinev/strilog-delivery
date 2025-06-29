package com.strilog.delivery.sender.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

public class SendFile {

    private static final Logger LOG = LoggerFactory.getLogger( SendFile.class );

    private final String     baseUrl;
    private final String     basicAuth;
    private final HttpClient httpClient;

    public SendFile(String baseUrl, String basicAuth) {
        this.baseUrl    = baseUrl;
        this.basicAuth  = basicAuth;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(ofSeconds(30))
                .build();
    }

    public void sendFile(File aFile) {
        URI uri = createUri(aFile);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(ofMinutes(10))
                .header("Authorization", basicAuth)
                .POST(createBodyPublisher(aFile))
                .build();

        LOG.debug("Sending {} bytes {} to {}", aFile.length(), aFile.getAbsolutePath(), uri);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Wrong response status " + response.statusCode() + " " + response.body());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot send to " + uri, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted send to " + uri, e);
        }
    }

    private static HttpRequest.BodyPublisher createBodyPublisher(File aFile) {
        try {
            return HttpRequest.BodyPublishers.ofFile(aFile.toPath());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot create body publisher for file " + aFile.getAbsolutePath(), e);
        }
    }

    private URI createUri(File aFile) {
        String url = baseUrl + "/" + aFile.getName();
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot create uri for " + url, e);
        }
    }

}
