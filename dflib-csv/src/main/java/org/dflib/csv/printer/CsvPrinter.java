package org.dflib.csv.printer;

import org.dflib.DataFrame;

import java.io.OutputStream;

public interface CsvPrinter {
    /**
     * Writes the given DataFrame as CSV to the target {@link Appendable}. Compression settings are ignored for
     * character-based sinks.
     *
     * @param df  DataFrame to write
     * @param out character sink to append encoded CSV to
     */
    void write(DataFrame df, Appendable out);

    /**
     * Writes the given DataFrame as CSV to the target {@link OutputStream}, applying the configured encoding and
     * compression codec (if any).
     *
     * @param df  DataFrame to write
     * @param out byte sink to write encoded (and optionally compressed) CSV to
     */
    void write(DataFrame df, OutputStream out);
}
