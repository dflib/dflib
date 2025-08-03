package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.tar.format.TarEntry;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A connector to work with TAR files.
 *
 * @since 2.0.0
 */
public abstract class Tar {

    public static Tar of(String fileName) {
        return of(new File(fileName));
    }

    public static Tar of(Path path) {
        return of(path.toFile());
    }

    public static Tar of(File file) {
        return new RandomAccessTar(file, null, null, notHiddenFilter());
    }

    public static Tar of(ByteSource parentSource) {
        return new SequentialTar(parentSource, null, null, notHiddenFilter());
    }

    protected final Predicate<TarEntry> filter;
    protected final Predicate<TarEntry> extFilter;
    protected final Predicate<TarEntry> notHiddenFilter;

    protected Tar(Predicate<TarEntry> filter, Predicate<TarEntry> extFilter, Predicate<TarEntry> notHiddenFilter) {
        this.filter = filter;
        this.extFilter = extFilter;
        this.notHiddenFilter = notHiddenFilter;
    }

    /**
     * Returns an instance of Tar that will only include entries that match the provided predicate. Any other
     * preconfigured filters (specific extensions or inclusion of hidden files) are also applied.
     */
    public abstract Tar include(Predicate<TarEntry> filter);

    public abstract Tar includeHidden();

    /**
     * Returns an instance of Tar that will filter archive files by extension.
     */
    public abstract Tar includeExtension(String ext);

    public List<TarEntry> list() {
        return list(false);
    }

    /**
     * Returns a list of all tar entries, including directories.
     */
    public abstract List<TarEntry> list(boolean includeFolders);

    public abstract ByteSource source(String tarPath);

    public abstract ByteSources sources();

    protected Predicate<TarEntry> combinedFilter(boolean includeFolders) {
        Predicate<TarEntry> entryTypeFilter = includeFolders ? p -> true : e -> !e.isDirectory();
        return Stream.of(filter, extFilter, notHiddenFilter)
                .filter(f -> f != null)
                .reduce(entryTypeFilter, Predicate::and);
    }

    static Predicate<TarEntry> extensionFilter(String ext) {
        Objects.requireNonNull(ext);
        if (ext.isEmpty()) {
            throw new IllegalArgumentException("Empty extension");
        }

        String normalizedExt = ext.startsWith(".") ? ext : "." + ext;
        return e -> e.getName().endsWith(normalizedExt);
    }

    static Predicate<TarEntry> notHiddenFilter() {
        return e -> {
            String name = e.getName();
            if (name.isEmpty()) {
                return true;
            }

            // "./" is not an indicator of a hidden file
            if (name.startsWith("./")) {
                name = name.substring(2);
            }

            for (String part : name.split("/")) {
                if (!part.isEmpty() && part.charAt(0) == '.') {
                    return false;
                }
            }

            return true;
        };
    }
}