package org.dflib.zip;

import org.dflib.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A ByteSource that seeks for a single ZipEntry in a stream
 *
 * @since 2.0.0
 */
class ZipStreamSeekByteSource implements ByteSource {

    private final ByteSource zipSource;
    private final String zipEntryName;

    public ZipStreamSeekByteSource(ByteSource zipSource, String zipEntryName) {
        this.zipEntryName = Objects.requireNonNull(zipEntryName);
        this.zipSource = Objects.requireNonNull(zipSource);
    }

    @Override
    public InputStream stream() {

        ZipInputStream in = new ZipInputStream(zipSource.stream());
        ZipEntry entry;

        try {
            while ((entry = in.getNextEntry()) != null) {
                if (zipEntryName.equals(entry.getName())) {

                    if (entry.isDirectory()) {
                        throw new IllegalArgumentException("Zip entry is a directory and can not be used in a ByteSource: " + zipEntryName);
                    }

                    return in;
                }
            }
        } catch (IOException e) {
            try {
                in.close();
            } catch (IOException ex) {
                // ignore
            }

            throw new RuntimeException("Error reading ZIP stream", e);
        }

        throw new RuntimeException("Entry is not present in the ZIP: " + zipEntryName);
    }
}
