package org.dflib.tar.format;

import java.util.Objects;

/**
 * A {@code struct sparse} in a <a href="https://www.gnu.org/software/tar/manual/html_node/Standard.html">Tar archive</a>.
 * Whereas, "struct sparse" is:
 * </p>
 * <pre>
 * struct sparse {
 * char offset[12];   // offset 0
 * char numbytes[12]; // offset 12
 * };
 * </pre>
 *
 * @since 2.0.0
 */
public class TarStructSparse {

    private final long offset;
    private final long numbytes;

    public TarStructSparse(long offset, long numBytes) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must not be negative");
        }
        if (numBytes < 0) {
            throw new IllegalArgumentException("numbytes must not be negative");
        }
        this.offset = offset;
        this.numbytes = numBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TarStructSparse that = (TarStructSparse) o;
        return offset == that.offset && numbytes == that.numbytes;
    }

    /**
     * Gets the byte count.
     *
     * @return the byte count.
     */
    public long getNumbytes() {
        return numbytes;
    }

    /**
     * Gets the offset.
     *
     * @return the offset.
     */
    public long getOffset() {
        return offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, numbytes);
    }

    @Override
    public String toString() {
        return "TarArchiveStructSparse{offset=" + offset + ", numbytes=" + numbytes + '}';
    }
}
