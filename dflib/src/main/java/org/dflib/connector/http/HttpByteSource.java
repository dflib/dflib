package org.dflib.connector.http;

import org.dflib.ByteSource;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @since 2.0.0
 */
class HttpByteSource implements ByteSource {

    private final HttpClient client;
    private final HttpRequest request;

    public HttpByteSource(HttpClient client, HttpRequest request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public InputStream stream() {
        HttpResponse<InputStream> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Error reading HTTP response", e);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Bad response from the server for '" + request.uri() + "': " + response.statusCode());
        }

        return response.body();
    }
}
