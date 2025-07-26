package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class RandomAccessZip extends Zip {

    private final ZipFile zipFile;

    public RandomAccessZip(ZipFile zipFile, Predicate<ZipEntry> extFilter, Predicate<ZipEntry> notHiddenFilter) {
        super(extFilter, notHiddenFilter);
        this.zipFile = Objects.requireNonNull(zipFile);
    }

    @Override
    public List<ZipEntry> list(boolean includeFolders) {
        return zipFile.stream()
                .filter(combinedFilter(includeFolders))
                .collect(Collectors.toList());
    }

    @Override
    public Zip includeHidden() {
        return new RandomAccessZip(zipFile, extFilter, null);
    }

    @Override
    public Zip includeExtension(String ext) {
        return new RandomAccessZip(zipFile, extensionFilter(ext), notHiddenFilter);
    }

    @Override
    public ByteSource source(String zipPath) {

        if (zipPath.endsWith("/")) {
            throw new IllegalArgumentException("Zip entry is a directory and can not be used to create a ByteSource: " + zipPath);
        }

        return list(false)
                .stream()
                .filter(e -> zipPath.equals(e.getName()))
                .findFirst()
                .map(e -> new ZipFileByteSource(zipFile, e))
                .orElseThrow(() -> new IllegalArgumentException("Entry is not present in the ZIP: " + zipPath));
    }

    @Override
    public ByteSources sources() {
        Map<String, ByteSource> sources = list(false)
                .stream()
                .collect(Collectors.toMap(ZipEntry::getName, e -> new ZipFileByteSource(zipFile, e)));

        return ByteSources.of(sources);
    }
}
