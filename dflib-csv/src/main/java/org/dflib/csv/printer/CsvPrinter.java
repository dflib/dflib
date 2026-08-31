package org.dflib.csv.printer;

import org.dflib.DataFrame;

import java.io.OutputStream;

public interface CsvPrinter {

    /**
     * Writes the given DataFrame as CSV to the target {@link Appendable}.
     *
     * @param df  DataFrame to write
     * @param out character sink to append encoded CSV to
     */
    void write(DataFrame df, Appendable out);

    /**
     * Writes the given DataFrame as CSV to the target {@link OutputStream}.
     *
     * @param df  DataFrame to write
     * @param out byte sink to write encoded CSV to
     */
    void write(DataFrame df, OutputStream out);
}
