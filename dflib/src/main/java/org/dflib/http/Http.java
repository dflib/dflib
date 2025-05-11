package org.dflib.http;

import org.dflib.ByteSource;
import org.dflib.Environment;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * A connector to access remote data over HTTP. A simpler version of this connector is {@link ByteSource#ofUrl(URL)}
 * method that allows to read data from a public URL, but does not allow to set headers (potentially needed for
 * authentication) or build URLs incrementally.
 *
 * @since 1.1.0
 */
public class Http {

    final String url;
    final Map<String, Set<Object>> headers;
    final Map<String, Set<Object>> queryParams;
    final HttpClient client;

    public static Http of(String url) {
        return new Http(url, Map.of(), Map.of(), null);
    }

    private Http(
            String url,
            Map<String, Set<Object>> headers,
            Map<String, Set<Object>> queryParams,
            HttpClient client) {
        this.url = Objects.requireNonNull(url);
        this.headers = Objects.requireNonNull(headers);
        this.queryParams = Objects.requireNonNull(queryParams);

        // can be null
        this.client = client;
    }

    /**
     * Returns a new connector with the provided path appended to this connector's path.
     */
    public Http path(String path) {
        return new Http(concatUrls(url, path), headers, queryParams, client);
    }

    /**
     * Returns a new connector with the provided query parameter combined with this connector's parameters.
     */
    public Http queryParam(String name, Object... values) {
        return new Http(url, headers, appendToMap(queryParams, name, values), client);
    }

    /**
     * Returns a new connector with the provided header combined with this connector's headers.
     */
    public Http header(String name, Object values) {
        return new Http(url, appendToMap(headers, name, values), queryParams, client);
    }

    /**
     * Returns a new connector with an explicitly provided HttpClient. If this method is not called, the client
     * is taken from the common {@link Environment}.
     *
     * @since 2.0.0
     */
    public Http client(HttpClient client) {
        return new Http(url, headers, queryParams, client);
    }

    public ByteSource source() {

        // environment client is initialized lazily, so resolve it at the very last moment
        // TODO: pass Supplier<HttpClient> down to the ByteSource?
        HttpClient client = this.client != null ? this.client : Environment.commonEnv().httpClient();

        return new HttpByteSource(client, createRequest());
    }

    private HttpRequest createRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(createURI()));
        headers.forEach((n, vs) -> vs.forEach(v -> builder.header(n, encodeHeader(v))));

        return builder.build();
    }

    String createURI() {
        if (queryParams.isEmpty()) {
            return url;
        }

        StringBuilder uri = new StringBuilder(url);

        // if the base URL already contains query parameters, respect that...
        String tail = uri.indexOf("?") > 0 ? "&" : "?";
        if (!url.endsWith(tail)) {
            uri.append(tail);
        }

        queryParams.forEach((n, vs) -> vs.forEach(v -> uri.append(n).append("=").append(encodeQueryParam(v)).append("&")));
        return uri.deleteCharAt(uri.length() - 1).toString();
    }

    private static String encodeHeader(Object value) {
        // leaving any real encoding to the caller...
        return value != null ? value.toString() : "";
    }

    private static String encodeQueryParam(Object value) {
        return value != null ? URLEncoder.encode(value.toString(), StandardCharsets.UTF_8) : "";
    }

    static String concatUrls(String base, String path) {

        if (base == null || base.length() == 0) {
            base = "/";
        }

        if (path == null || path.length() == 0) {
            return base;
        }

        if (!base.endsWith("/")) {
            base = base + "/";
        }

        if (path == null) {
            path = "";
        } else if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return base + path;
    }

    static Map<String, Set<Object>> appendToMap(Map<String, Set<Object>> source, String key, Object... vals) {
        if (vals == null || vals.length == 0) {
            return source;
        }

        // do a deep clone, including value sets.. Use a map with predictable ordering
        Map<String, Set<Object>> result = new LinkedHashMap<>((int) (1 + source.size() / 0.75));
        source.forEach((k, v) -> result.put(k, new LinkedHashSet<>(v)));
        result.computeIfAbsent(key, k -> new LinkedHashSet<>()).addAll(asList(vals));

        return result;
    }
}
