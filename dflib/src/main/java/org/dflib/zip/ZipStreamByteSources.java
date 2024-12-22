package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @since 2.0.0
 */
class ZipStreamByteSources implements ByteSources {

    private final ByteSource zipSource;

    public ZipStreamByteSources(ByteSource zipSource) {
        this.zipSource = zipSource;
    }

    @Override
    public <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor) {

        // have to process ZipInputStream sequentially
        // TODO: does it make sense in certain situations to open multiple streams, and scan through to specific entries
        //  to do parallel loading?

        Map<String, T> result = new HashMap<>();
        try (ZipInputStream in = new ZipInputStream(zipSource.stream())) {

            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    continue;
                }

                T val = processor.apply(entry.getName(), new ZipStreamByteSource(in));
                result.put(entry.getName(), val);
                in.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading ZIP stream", e);
        }

        return result;
    }
}
