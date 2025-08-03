package org.dflib.tar.format;

import java.io.InputStream;

/**
 * An InputStream that always return 0, this is used when reading the "holes" of a sparse file
 *
 * @since 2.0.0
 */
class TarSparseZeroInputStream extends InputStream {

    /**
     * Returns 0.
     */
    @Override
    public int read() {
        return 0;
    }

    /**
     * Returns the input.
     */
    @Override
    public long skip(long n) {
        return n;
    }
}
