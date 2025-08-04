package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.codec.Codec;
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

    private final ByteSource src;
    private final String tarEntryName;
    private final Codec compressionCodec;

    public TarStreamSeekByteSource(ByteSource src, String tarEntryName, Codec compressionCodec) {
        this.src = Objects.requireNonNull(src);
        this.tarEntryName = Objects.requireNonNull(tarEntryName);
        this.compressionCodec = compressionCodec;
    }

    @Override
    public Optional<String> uri() {
        // TODO: should we include the name of the tarSource to provide the full identifier?
        return Optional.of(tarEntryName);
    }

    @Override
    public InputStream stream() {
        Codec codec = this.compressionCodec != null
                ? this.compressionCodec
                : Codec.ofUri(src.uri().orElse("")).orElse(null);

        ByteSource plainSrc = codec != null ? src.decompress(codec) : src;

        TarInputStream in = new TarInputStream(plainSrc.stream());
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