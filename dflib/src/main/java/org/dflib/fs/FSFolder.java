package org.dflib.fs;

import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a folder in a local filesystem. Allows to work with multiple files in the folder as {@link ByteSources},
 * list and filter folder contents, etc.
 *
 * @since 2.0.0
 */
public class FSFolder {

    public static FSFolder of(String fileName) {
        return of(new File(fileName));
    }

    public static FSFolder of(File file) {
        return of(file.toPath());
    }

    public static FSFolder of(Path path) {
        return new FSFolder(
                path,
                false,
                null,
                null,
                notHiddenFilter());
    }

    private final Path folderPath;
    private final boolean includeSubfolders;
    private final Predicate<Path> filter;
    private final Predicate<Path> extFilter;
    private final Predicate<Path> notHiddenFilter;

    private FSFolder(
            Path folderPath,
            boolean includeSubfolders,
            Predicate<Path> filter,
            Predicate<Path> extFilter,
            Predicate<Path> notHiddenFilter) {

        this.folderPath = Objects.requireNonNull(folderPath);
        this.includeSubfolders = includeSubfolders;
        this.filter = filter;
        this.extFilter = extFilter;
        this.notHiddenFilter = notHiddenFilter;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    /**
     * Returns an instance of FSFolder that is a subfolder of this folder.
     */
    public FSFolder path(String subfolder) {
        Path other = Path.of(subfolder);
        if (other.isAbsolute()) {
            throw new IllegalArgumentException("Subfolder path can not use absolute path: " + subfolder);
        }

        Path subfolderPath = folderPath.resolve(other);
        return new FSFolder(subfolderPath, includeSubfolders, filter, extFilter, notHiddenFilter);
    }

    /**
     * Returns an instance of FSFolder that will only include files that match the provided predicate. Any other
     * preconfigured filters (specific extensions or inclusion of hidden files) are also applied.
     */
    public FSFolder includePaths(Predicate<Path> filter) {
        Objects.requireNonNull(filter);
        return new FSFolder(folderPath, includeSubfolders, filter, extFilter, notHiddenFilter);
    }

    public FSFolder includeHidden() {
        return new FSFolder(folderPath, includeSubfolders, filter, extFilter, null);
    }

    /**
     * Returns an instance of FSFolder that will filter folder files by extension.
     */
    public FSFolder includeExtension(String ext) {
        return new FSFolder(
                folderPath,
                includeSubfolders,
                filter,
                extensionFilter(ext),
                notHiddenFilter);
    }

    /**
     * Returns an instance of FSFolder that will include files in subfolders.
     */
    public FSFolder includeSubfolders() {
        return this.includeSubfolders ? this : new FSFolder(folderPath, true, filter, extFilter, notHiddenFilter);
    }

    /**
     * Returns a single resource within the folder. Since the name is explicit, the resource doesn't need to match
     * folder filters. E.g., a hidden file can be resolved even if the folder listing is not showing them. Same goes
     * for extension filters, etc.
     */
    public ByteSource source(String path) {
        return source(Path.of(path));
    }

    /**
     * Returns a single resource within the folder. Since the name is explicit, the resource doesn't need to match
     * folder filters. E.g., a hidden file can be resolved even if the folder listing is not showing them. Same goes
     * for extension filters, etc.
     */
    public ByteSource source(Path subPath) {
        return ByteSource.ofPath(folderPath.resolve(subPath));
    }

    /**
     * Returns all files contained in the folder represented as {@link ByteSources}. The contents are filtered
     * according to FSFolder configuration.
     */
    public ByteSources sources() {
        Map<String, ByteSource> sources = new HashMap<>();

        // The key must be a relative to the folder, whereas the Path for ByteSource must be the full Path
        list().forEach(p -> sources.put(folderPath.relativize(p).toString(), ByteSource.ofPath(p)));
        return ByteSources.of(sources);
    }

    public List<Path> list() {
        return list(false);
    }

    /**
     * Returns the list of all paths contained in the folder, filtered according to FSFolder configuration.
     */
    public List<Path> list(boolean includeFolders) {

        File root = folderPath.toFile();
        if (!root.isDirectory()) {
            throw new RuntimeException("Not a folder: " + folderPath);
        }

        // TODO: configurable symlink option
        // The defaults are to allow symlinks for files but not for directories

        try {
            return Files.walk(folderPath, includeSubfolders ? Integer.MAX_VALUE : 1)
                    .filter(combinedFilter(includeFolders))
                    // ensure stable, predictable processing order
                    .sorted()

                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Predicate<Path> combinedFilter(boolean includeFolders) {
        Predicate<Path> pathTypeFilter = includeFolders ? p -> true : Files::isRegularFile;
        return Stream.of(filter, extFilter, notHiddenFilter)
                .filter(f -> f != null)
                .reduce(pathTypeFilter, Predicate::and);
    }

    static Predicate<Path> extensionFilter(String ext) {
        Objects.requireNonNull(ext);
        if (ext.isEmpty()) {
            throw new IllegalArgumentException("Empty extension");
        }

        String normalizedExt = ext.startsWith(".") ? ext : "." + ext;
        return p -> p.getFileName().toString().endsWith(normalizedExt);
    }

    static Predicate<Path> notHiddenFilter() {
        return p -> {
            String name = p.getFileName().toString();
            return name.length() > 1 && name.charAt(0) != '.';
        };
    }
}
