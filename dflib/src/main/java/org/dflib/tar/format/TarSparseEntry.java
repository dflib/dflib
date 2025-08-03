package org.dflib.tar.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A sparse entry in a <a href="https://www.gnu.org/software/tar/manual/html_node/Standard.html">Tar archive</a>.
 *
 * <p>
 * The C structure for a sparse entry is:
 *
 * <pre>
 * struct posix_header {
 * struct sparse sp[21]; // TarConstants.SPARSELEN_GNU_SPARSE     - offset 0
 * char isextended;      // TarConstants.ISEXTENDEDLEN_GNU_SPARSE - offset 504
 * };
 * </pre>
 * <p>
 * Whereas, "struct sparse" is:
 *
 * <pre>
 * struct sparse {
 * char offset[12];   // offset 0
 * char numbytes[12]; // offset 12
 * };
 * </pre>
 *
 * <p>
 * Each such struct describes a block of data that has actually been written to the archive. The offset describes where in the extracted file the data is
 * supposed to start and the numbytes provides the length of the block. When extracting the entry the gaps between the sparse structs are equivalent to areas
 * filled with zero bytes.
 * </p>
 *
 *  @since 2.0.0
 */
class TarSparseEntry {

    // If an extension sparse header follows.
    private final boolean isExtended;

    private final List<TarStructSparse> sparseHeaders;

    public TarSparseEntry(byte[] headerBuf) throws IOException {
        this.sparseHeaders = new ArrayList<>(TarUtils.readSparseStructs(headerBuf, 0, TarConstants.SPARSE_HEADERS_IN_EXTENSION_HEADER));
        this.isExtended = TarUtils.parseBoolean(headerBuf, TarConstants.SPARSELEN_GNU_SPARSE);
    }

    /**
     * Gets information about the configuration for the sparse entry.
     */
    public List<TarStructSparse> getSparseHeaders() {
        return sparseHeaders;
    }

    /**
     * Tests whether this entry is extended.
     *
     * @return whether this entry is extended.
     */
    public boolean isExtended() {
        return isExtended;
    }
}
