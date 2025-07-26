package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class SequentialZip extends Zip {

    private final ByteSource source;

    public SequentialZip(ByteSource source, Predicate<ZipEntry> filter, Predicate<ZipEntry> extFilter, Predicate<ZipEntry> notHiddenFilter) {
        super(filter, extFilter, notHiddenFilter);
        this.source = source;
    }

    @Override
    public List<ZipEntry> list(boolean includeFolders) {
        Predicate<ZipEntry> filter = combinedFilter(includeFolders);
        return source.processStream(in -> extractEntries(in, filter));
    }

    @Override
    public Zip include(Predicate<ZipEntry> filter) {
        Objects.requireNonNull(filter);
        return new SequentialZip(source, filter, extFilter, notHiddenFilter);
    }

    @Override
    public Zip includeHidden() {
        return new SequentialZip(source, filter, extFilter, null);
    }

    @Override
    public Zip includeExtension(String ext) {
        return new SequentialZip(source, filter, extensionFilter(ext), notHiddenFilter);
    }

    @Override
    public ByteSource source(String zipPath) {

        if (zipPath.endsWith("/")) {
            throw new IllegalArgumentException("Zip entry is a directory and can not be used to create a ByteSource: " + zipPath);
        }

        return new ZipStreamSeekByteSource(source, zipPath);
    }

    @Override
    public ByteSources sources() {
        return new ZipStreamByteSources(source, combinedFilter(false));
    }

    private List<ZipEntry> extractEntries(InputStream in, Predicate<ZipEntry> filter) {
        ZipInputStream zin = new ZipInputStream(in);
        List<ZipEntry> entries = new ArrayList<>();
        ZipEntry entry;

        try {
            while ((entry = zin.getNextEntry()) != null) {
                if (filter.test(entry)) {
                    entries.add(entry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip entries", e);
        }

        return entries;
    }
}
