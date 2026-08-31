package org.dflib.csv.printer;

import java.io.IOException;

/**
 * Encodes a single CSV field to its textual representation.
 *
 * @since 2.0.0
 */
public interface CsvFieldEncoder {

    /**
     * Appends the encoded representation of the given value to the target.
     *
     * @param value value to encode
     * @param out target to append to
     */
    void encode(Object value, Appendable out) throws IOException;
}
