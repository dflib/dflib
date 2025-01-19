package org.dflib;

import org.dflib.codec.Codec;
import org.dflib.http.Http;
import org.dflib.zip.Zip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
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
        try (InputStream in = stream()) {
            return processor.apply(in);
        } catch (IOException e) {

            // presumably IOException in this situation is only possible on close. All other exceptions won't be of the
            // checked kind
            throw new RuntimeException("Error closing stream", e);
        }
    }

    /**
     * Assuming this source represents a ZIP archive, returns a {@link ByteSources} catalog of the archive entries.
     *
     * @since 2.0.0
     */
    default ByteSources unzip() {
        return Zip.of(this).sources();
    }

    /**
     * Assuming the source represents a compressed stream, returns another source that will provide decompressed streams
     * to the callers. See {@link Codec} for supported decompression algorithms.
     *
     * @since 2.0.0
     */
    default ByteSource decompress(Codec codec) {
        return codec.decompress(this);
    }

    default byte[] asBytes() {
        try (InputStream in = stream()) {
            return in.readAllBytes();
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

    /**
     * Returns a ByteSource based on the Path to a local file.
     *
     * @since 2.0.0
     */
    static ByteSource ofPath(Path path) {
        return ofFile(path.toFile());
    }

    /**
     * Returns a ByteSource based on the local file.
     *
     * @since 2.0.0
     */
    static ByteSource ofFile(String file) {
        return ofFile(new File(file));
    }

    /**
     * Returns a ByteSource based on the local file.
     *
     * @since 2.0.0
     */
    static ByteSource ofFile(File file) {
        return () -> {
            try {
                return new FileInputStream(file);
            } catch (IOException e) {
                throw new RuntimeException("Error reading file:" + file, e);
            }
        };
    }
}
