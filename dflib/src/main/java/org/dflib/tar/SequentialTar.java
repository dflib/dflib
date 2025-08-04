package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.codec.Codec;
import org.dflib.tar.format.TarInputStream;
import org.dflib.tar.format.TarEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Sequential TAR implementation that reads TAR entries from a stream.
 *
 * @since 2.0.0
 */
class SequentialTar extends Tar {

    private final ByteSource source;

    public SequentialTar(
            ByteSource source,
            Predicate<TarEntry> filter,
            Predicate<TarEntry> extFilter,
            Predicate<TarEntry> notHiddenFilter,
            Codec compressionCodec) {
        super(filter, extFilter, notHiddenFilter, compressionCodec);
        this.source = source;
    }

    @Override
    public List<TarEntry> list(boolean includeFolders) {
        Predicate<TarEntry> filter = combinedFilter(includeFolders);
        return source.processStream(in -> extractEntries(in, filter));
    }

    @Override
    public Tar include(Predicate<TarEntry> filter) {
        Objects.requireNonNull(filter);
        return new SequentialTar(source, filter, extFilter, notHiddenFilter, compressionCodec);
    }

    @Override
    public Tar includeHidden() {
        return new SequentialTar(source, filter, extFilter, null, compressionCodec);
    }

    @Override
    public Tar includeExtension(String ext) {
        return new SequentialTar(source, filter, extensionFilter(ext), notHiddenFilter, compressionCodec);
    }

    @Override
    public Tar compression(Codec codec) {
        return new SequentialTar(source, filter, extFilter, notHiddenFilter, codec);
    }

    @Override
    public ByteSource source(String tarPath) {
        if (tarPath.endsWith("/")) {
            throw new IllegalArgumentException("TAR entry is a directory and cannot be used to create a ByteSource: " + tarPath);
        }

        return new TarStreamSeekByteSource(source, tarPath, compressionCodec);
    }

    @Override
    public ByteSources sources() {
        return new TarStreamByteSources(source, combinedFilter(false), compressionCodec);
    }

    private List<TarEntry> extractEntries(InputStream in, Predicate<TarEntry> filter) {
        try (TarInputStream tin = new TarInputStream(in)) {
            List<TarEntry> entries = new ArrayList<>();
            TarEntry entry;

            while ((entry = tin.getNextEntry()) != null) {
                if (filter.test(entry)) {
                    entries.add(entry);
                }
            }

            return entries;
        } catch (IOException e) {
            throw new RuntimeException("Error reading TAR entries", e);
        }
    }
}