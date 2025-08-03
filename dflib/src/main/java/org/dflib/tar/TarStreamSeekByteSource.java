package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.tar.format.TarInputStream;
import org.dflib.tar.format.TarEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * A ByteSource that seeks for a single TarEntry in a stream.
 *
 * @since 2.0.0
 */
class TarStreamSeekByteSource implements ByteSource {

    private final ByteSource tarSource;
    private final String tarEntryName;

    public TarStreamSeekByteSource(ByteSource tarSource, String tarEntryName) {
        this.tarEntryName = Objects.requireNonNull(tarEntryName);
        this.tarSource = Objects.requireNonNull(tarSource);
    }

    @Override
    public Optional<String> uri() {
        // TODO: should we include the name of the tarSource to provide the full identifier?
        return Optional.of(tarEntryName);
    }

    @Override
    public InputStream stream() {
        TarInputStream in = new TarInputStream(tarSource.stream());
        TarEntry entry;

        try {
            while ((entry = in.getNextEntry()) != null) {
                if (tarEntryName.equals(entry.getName())) {
                    if (entry.isDirectory()) {
                        throw new IllegalArgumentException("TAR entry is a directory and cannot be used in a ByteSource: " + tarEntryName);
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

            throw new RuntimeException("Error reading TAR stream", e);
        }

        throw new RuntimeException("Entry is not present in the TAR: " + tarEntryName);
    }
}