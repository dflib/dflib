package org.dflib.tar;

import org.dflib.ByteSource;
import org.dflib.tar.format.TarEntry;
import org.dflib.tar.format.TarFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * A ByteSource based on random access TarEntry in a TarFile.
 *
 * @since 2.0.0
 */
class TarFileByteSource implements ByteSource {

    private final File file;
    private final TarEntry tarEntry;

    public TarFileByteSource(File file, TarEntry tarEntry) {
        this.file = file;
        this.tarEntry = tarEntry;
    }

    @Override
    public Optional<String> uri() {
        // TODO: should we include the name of the TarFile to provide the full identifier?
        return Optional.of(tarEntry.getName());
    }

    @Override
    public InputStream stream() {

        // TODO: Performance!! Reopening TarFile every time is expensive... We should either directly read chunks of
        //  data from .tar via some static method or should make TarFile reentrant (ZipFile seems to be reentrant).

        try {
            return new SingleEntryStream(new TarFile(file), tarEntry);
        } catch (IOException e) {
            throw new RuntimeException("Error getting a stream for TAR entry: " + tarEntry.getName(), e);
        }
    }

    // ensures closing
    static class SingleEntryStream extends InputStream {

        private final TarFile tarFile;
        private final InputStream delegate;

        public SingleEntryStream(TarFile tarFile, TarEntry entry) throws IOException {
            this.tarFile = tarFile;
            this.delegate = tarFile.getInputStream(entry);
        }

        @Override
        public void close() throws IOException {
            delegate.close();
            tarFile.close();
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public byte[] readNBytes(int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public int readNBytes(byte[] b, int off, int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }
    }
}