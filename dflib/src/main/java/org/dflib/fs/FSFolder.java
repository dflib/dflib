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

/**
 * Represents a folder on a locally-available filesystem. Allows to work with multiple files in the folder as
 * {@link ByteSources}, list and filter folder contents, etc.
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
        return new FSFolder(path, false, null);
    }

    private final Path folderPath;
    private final boolean includeSubfolders;
    private final Predicate<Path> filter;

    private FSFolder(Path folderPath, boolean includeSubfolders, Predicate<Path> filter) {
        this.folderPath = Objects.requireNonNull(folderPath);
        this.filter = filter;
        this.includeSubfolders = includeSubfolders;
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
        return new FSFolder(subfolderPath, includeSubfolders, filter);
    }

    /**
     * Returns an instance of FSFolder that will filter folder files by extension. All the existing filters of this
     * folder will be ignored.
     */
    public FSFolder includeExtension(String ext) {
        Objects.requireNonNull(ext);
        if (ext.length() == 0) {
            throw new IllegalArgumentException("Empty extension");
        }

        String normalizedExt = ext.startsWith(".") ? ext : "." + ext;
        return new FSFolder(folderPath, includeSubfolders, p -> p.getFileName().toString().endsWith(normalizedExt));
    }

    /**
     * Returns an instance of FSFolder that will include files in subfolders.
     */
    public FSFolder includeSubfolders() {
        return this.includeSubfolders ? this : new FSFolder(folderPath, true, filter);
    }

    /**
     * Returns all files contained in the folder represented as {@link ByteSources}. The contents are filtered
     * according to FSFolder configuration.
     */
    public ByteSources sources() {
        Map<String, ByteSource> sources = new HashMap<>();

        // The key must be a relative to the folder, whereas the Path for ByteSource must be the full Path
        list().stream().forEach(p -> sources.put(folderPath.relativize(p).toString(), ByteSource.ofPath(p)));
        return ByteSources.of(sources);
    }

    /**
     * Returns the list of all paths contained in the folder, filtered according to FSFolder configuration.
     */
    public List<Path> list() {

        File root = folderPath.toFile();
        if (!root.isDirectory()) {
            throw new RuntimeException("Not a folder: " + folderPath);
        }

        // TODO: configurable symlink option
        // The defaults are to allow symlinks for files but not for directories

        Predicate<Path> fileFilter = p -> Files.isRegularFile(p);
        Predicate<Path> filter = this.filter != null
                ? fileFilter.and(this.filter)
                : fileFilter;

        try {
            return Files.walk(folderPath, includeSubfolders ? Integer.MAX_VALUE : 1)
                    .filter(filter)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
