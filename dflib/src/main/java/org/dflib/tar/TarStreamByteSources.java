package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.codec.Codec;
import org.dflib.tar.format.TarInputStream;
import org.dflib.tar.format.TarEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A ByteSources implementation for TAR streams.
 *
 * @since 2.0.0
 */
class TarStreamByteSources implements ByteSources {

    private final ByteSource src;
    private final Predicate<TarEntry> filter;
    private final Codec compressionCodec;

    public TarStreamByteSources(ByteSource src, Predicate<TarEntry> filter, Codec compressionCodec) {
        this.src = src;
        this.filter = filter;
        this.compressionCodec = compressionCodec;
    }

    @Override
    public <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor) {

        Codec codec = this.compressionCodec != null
                ? this.compressionCodec
                : Codec.ofUri(src.uri().orElse("")).orElse(null);

        ByteSource plainSrc = codec != null ? src.decompress(codec) : src;

        // Have to process TarArchiveInputStream sequentially
        // TODO: does it make sense in certain situations to open multiple streams, and scan through to specific entries
        //  to do parallel loading?

        Map<String, T> result = new HashMap<>();
        try (TarInputStream in = new TarInputStream(plainSrc.stream())) {

            TarEntry entry;
            while ((entry = in.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    continue;
                }

                if (!filter.test(entry)) {
                    continue;
                }

                T val = processor.apply(entry.getName(), new TarStreamByteSource(in, entry.getName()));
                result.put(entry.getName(), val);
                
                // Note: TAR doesn't have a closeEntry() method like ZIP, 
                // the stream automatically moves to the next entry when this one is fully read
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading TAR stream", e);
        }

        return result;
    }
}