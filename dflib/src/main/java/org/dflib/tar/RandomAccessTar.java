package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.tar.format.TarEntry;
import org.dflib.tar.format.TarFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Random access TAR implementation that provides indexed access to TAR entries.
 *
 * @since 2.0.0
 */
class RandomAccessTar extends Tar {

    // Referencing the archive as a File object. TarFile is not reentrant and must be created and closed on the spot
    private final File file;

    public RandomAccessTar(
            File file,
            Predicate<TarEntry> filter,
            Predicate<TarEntry> extFilter,
            Predicate<TarEntry> notHiddenFilter) {

        super(filter, extFilter, notHiddenFilter);
        this.file = file;
    }

    @Override
    public List<TarEntry> list(boolean includeFolders) {
        try (TarFile tf = createTarFile(file)) {
            return tf.getEntries()
                    .stream()
                    .filter(combinedFilter(includeFolders))
                    .collect(Collectors.toList());
        } catch (Exception closeEx) {
            throw new RuntimeException(closeEx);
        }
    }

    @Override
    public Tar include(Predicate<TarEntry> filter) {
        Objects.requireNonNull(filter);

        // Create a new instance with the additional filter
        // Note: We need to pass the same file, so we'll need to refactor this
        // For now, we'll create a filtered view
        return new RandomAccessTar(file, filter, extFilter, notHiddenFilter);
    }

    @Override
    public Tar includeHidden() {
        return new RandomAccessTar(file, filter, extFilter, null);
    }

    @Override
    public Tar includeExtension(String ext) {
        return new RandomAccessTar(file, filter, extensionFilter(ext), notHiddenFilter);
    }

    @Override
    public ByteSource source(String tarPath) {
        if (tarPath.endsWith("/")) {
            throw new IllegalArgumentException("TAR entry is a directory and cannot be used to create a ByteSource: " + tarPath);
        }

        return list(false)
                .stream()
                .filter(e -> tarPath.equals(e.getName()))
                .findFirst()
                .map(e -> new TarFileByteSource(file, e))
                .orElseThrow(() -> new IllegalArgumentException("Entry is not present in the TAR: " + tarPath));
    }

    @Override
    public ByteSources sources() {

        // TODO: Performance!! Since reopening TarFile every time is expensive, until #524 is fixed, using a streaming
        //  processor instead of random access

        return new TarStreamByteSources(ByteSource.ofFile(file), combinedFilter(false));
    }

    private static TarFile createTarFile(File file) {
        try {
            return new TarFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}