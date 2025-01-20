package org.dflib.zip;

import org.dflib.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A source based on random access ZipEntry in a ZipFile
 *
 * @since 2.0.0
 */
class ZipFileByteSource implements ByteSource {

    private final ZipFile zipFile;
    private final ZipEntry zipEntry;

    public ZipFileByteSource(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipEntry = zipEntry;
        this.zipFile = zipFile;
    }

    @Override
    public Optional<String> uri() {
        // TODO: should we include the name of the ZipFile to provide the full identifier?
        return Optional.of(zipEntry.getName());
    }

    public ZipEntry getZipEntry() {
        return zipEntry;
    }

    @Override
    public InputStream stream() {
        try {
            return zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            throw new RuntimeException("Error getting a stream for zip entry: " + zipEntry.getName(), e);
        }
    }
}
