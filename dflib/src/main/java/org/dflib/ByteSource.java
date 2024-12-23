package org.dflib;

import org.dflib.http.Http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

/**
 * Represents a binary resource that provides data for DataFrame(s). It is an abstraction between DataFrame
 * loaders and data storages. ByteSource helps to locate and read the resource, but does not interpret its data.
 * A ByteSource can be a file, an HTTP URL, a byte[], a cloud resource, etc. DFLib provides a number of ByteSource
 * types out of the box.
 *
 * @since 1.1.0
 */
@FunctionalInterface
public interface ByteSource {

    /**
     * Returns source data as a stream.
     */
    InputStream stream();

    default <T> T processStream(Function<InputStream, T> processor) {
        return processor.apply(stream());
    }

    default byte[] asBytes() {
        try {
            return stream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading stream", e);
        }
    }

    static ByteSource of(byte[] bytes) {
        return new ByteSource() {
            @Override
            public InputStream stream() {
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public byte[] asBytes() {
                // this is unsafe as the caller can change the array, but weighing it against making a  full in-memory
                // copy, let's compromise immutability
                return bytes;
            }
        };
    }

    /**
     * Returns a ByteSource mapped to a URL. For HTTP URLs also consider using {@link Http}
     * connector that supports setting headers and building URLs incrementally.
     *
     * @see Http
     */
    static ByteSource ofUrl(URL url) {
        return () -> {
            try {
                return url.openStream();
            } catch (IOException e) {
                throw new RuntimeException("Error reading the URL:" + url, e);
            }
        };
    }

    /**
     * Returns a ByteSource mapped to a URL.
     */
    static ByteSource ofUrl(String url) {
        try {
            return ofUrl(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Bad URL:" + url, e);
        }
    }
}
