package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * A connector to work with ZIP files.
 *
 * @since 2.0.0
 */
public abstract class Zip {

    public static Zip of(String fileName) {
        return of(new File(fileName));
    }

    public static Zip of(Path path) {
        return of(path.toFile());
    }

    public static Zip of(File file) {
        try {
            return new RandomAccessZip(new ZipFile(file));
        } catch (IOException e) {
            throw new RuntimeException("Error opening ZIP file: " + file, e);
        }
    }

    public static Zip of(ByteSource parentSource) {
        return new SequentialZip(parentSource);
    }

    /**
     * Returns a list of all zip entries, including directories.
     */
    public abstract List<ZipEntry> list();

    public abstract ByteSource source(String zipPath);

    public abstract ByteSources sources();

    static class SequentialZip extends Zip {

        private final ByteSource source;
        private volatile List<ZipEntry> index;

        public SequentialZip(ByteSource source) {
            this.source = source;
        }

        @Override
        public List<ZipEntry> list() {
            return getOrCreateIndex();
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
            return new ZipStreamByteSources(source);
        }

        private List<ZipEntry> getOrCreateIndex() {
            if (this.index == null) {
                synchronized (this) {
                    if (this.index == null) {
                        this.index = source.processStream(this::extractIndex);
                    }
                }
            }

            return index;
        }

        private List<ZipEntry> extractIndex(InputStream in) {
            ZipInputStream zin = new ZipInputStream(in);
            List<ZipEntry> entries = new ArrayList<>();
            ZipEntry entry;

            try {
                while ((entry = zin.getNextEntry()) != null) {
                    entries.add(entry);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading zip entries", e);
            }

            return entries;
        }
    }

    static class RandomAccessZip extends Zip {

        private final ZipFile zipFile;
        private volatile Map<String, ZipEntry> sources;

        public RandomAccessZip(ZipFile zipFile) {
            this.zipFile = Objects.requireNonNull(zipFile);
        }

        @Override
        public List<ZipEntry> list() {
            return getOrCreateSources().values()
                    .stream()
                    .collect(Collectors.toList());
        }

        @Override
        public ByteSource source(String zipPath) {
            ZipEntry entry = getOrCreateSources().get(zipPath);

            if (entry == null) {
                throw new IllegalArgumentException("Entry is not present in the ZIP: " + zipPath);
            }

            if (entry.isDirectory()) {
                throw new IllegalArgumentException("Zip entry is a directory and can not be used to create a ByteSource: " + zipPath);
            }

            return new ZipFileByteSource(zipFile, entry);
        }

        @Override
        public ByteSources sources() {
            Map<String, ByteSource> sources = new HashMap<>();

            // exclude directories
            getOrCreateSources()
                    .entrySet()
                    .stream()
                    .filter(e -> !e.getValue().isDirectory())
                    .forEach(e -> sources.put(e.getKey(), new ZipFileByteSource(zipFile, e.getValue())));

            return ByteSources.of(sources);
        }

        private Map<String, ZipEntry> getOrCreateSources() {
            if (this.sources == null) {
                synchronized (this) {
                    if (this.sources == null) {
                        Map<String, ZipEntry> index = new HashMap<>();
                        zipFile.stream().forEach(e -> index.put(e.getName(), e));
                        this.sources = index;
                    }
                }
            }

            return sources;
        }
    }
}
