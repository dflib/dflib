package org.dflib.zip;

import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
            return new RandomAccessZip(new ZipFile(file), null, notHiddenFilter());
        } catch (IOException e) {
            throw new RuntimeException("Error opening ZIP file: " + file, e);
        }
    }

    public static Zip of(ByteSource parentSource) {
        return new SequentialZip(parentSource, null, notHiddenFilter());
    }

    static Predicate<ZipEntry> extensionFilter(String ext) {
        Objects.requireNonNull(ext);
        if (ext.isEmpty()) {
            throw new IllegalArgumentException("Empty extension");
        }

        String normalizedExt = ext.startsWith(".") ? ext : "." + ext;
        return e -> e.getName().endsWith(normalizedExt);
    }

    static Predicate<ZipEntry> notHiddenFilter() {
        return e -> {
            String name = e.getName();
            if (name.length() == 0) {
                return true;
            }

            // TODO: presumably some Windows tools may use backslashes for separators
            String[] parts = name.split("/");
            int len = parts.length;
            for (int i = 0; i < len; i++) {
                if (parts[i].length() > 0 && parts[i].charAt(0) == '.') {
                    return false;
                }
            }

            return true;
        };
    }

    protected final Predicate<ZipEntry> extFilter;
    protected final Predicate<ZipEntry> notHiddenFilter;

    protected Zip(Predicate<ZipEntry> extFilter, Predicate<ZipEntry> notHiddenFilter) {
        this.extFilter = extFilter;
        this.notHiddenFilter = notHiddenFilter;
    }

    public abstract Zip includeHidden();

    /**
     * Returns an instance of Zip that will filter archive files by extension.
     */
    public abstract Zip includeExtension(String ext);

    public List<ZipEntry> list() {
        return list(false);
    }

    /**
     * Returns a list of all zip entries, including directories.
     */
    public abstract List<ZipEntry> list(boolean includeFolders);

    public abstract ByteSource source(String zipPath);

    public abstract ByteSources sources();

    protected Predicate<ZipEntry> entryFilter(boolean includeFolders) {
        Predicate<ZipEntry> entryTypeFilter = includeFolders ? p -> true : e -> !e.isDirectory();
        return Stream.of(extFilter, notHiddenFilter)
                .filter(f -> f != null)
                .reduce(entryTypeFilter, Predicate::and);
    }
}
