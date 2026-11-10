package org.dflib.csv.printer;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Quote;
import org.dflib.row.RowProxy;

import java.io.IOException;
import java.util.Objects;

/**
 * CSV printer main entry point, orchestrates the whole printing process.
 * Internal API. Part of the {@link org.dflib.csv.CsvSaver} API
 *
 * @since 2.0.0
 */
public final class CsvPrinter {

    private final boolean printHeader;
    private final FieldEncoder encoder;
    private final Quote quote;
    private final String delimiter;
    private final String lineBreak;
    private final boolean trailingDelimiter;
    private final boolean hasNullString;

    /**
     * @param format      CSV format to use
     * @param printHeader whether the DataFrame column names should be printed as the first CSV row
     */
    public CsvPrinter(CsvFormat format, boolean printHeader) {
        Objects.requireNonNull(format);

        this.printHeader = printHeader;
        this.encoder = new FieldEncoder(format);
        this.quote = format.quote();
        this.delimiter = new String(format.delimiter().asArray());
        this.lineBreak = lineBreakString(format);
        this.trailingDelimiter = format.trailingDelimiter();
        this.hasNullString = format.nullString() != null;
    }

    /**
     * Prints the given DataFrame as CSV to the target {@link Appendable}.
     *
     * @throws IllegalArgumentException if the DataFrame contains values that can't be represented in the format
     */
    public void print(DataFrame df, Appendable out) throws IOException {

        int width = df.width();
        if (width == 1 && !trailingDelimiter) {
            printSingleColumn(df, out);
            return;
        }

        if (printHeader) {
            printHeaderRow(df.getColumnsIndex(), out);
        }

        for (RowProxy row : df) {
            printRow(row, width, out);
        }
    }

    // single column records must never be empty lines, so each value is encoded to a buffer first to check for that
    private void printSingleColumn(DataFrame df, Appendable out) throws IOException {
        StringBuilder buffer = new StringBuilder();

        if (printHeader) {
            printSingleValue(df.getColumnsIndex().get(0), out, buffer);
        }

        for (Object value : df.getColumn(0)) {
            printSingleValue(value, out, buffer);
        }
    }

    private void printSingleValue(Object value, Appendable out, StringBuilder buffer) throws IOException {

        buffer.setLength(0);
        encoder.encodeAtLineStart(value, buffer);
        if (buffer.isEmpty()) {

            // an empty line is skipped by the parser, and a quoted empty string is never read as null
            if (value == null && hasNullString) {
                throw new IllegalArgumentException(
                        "Can't encode a null in a single-column CSV with an empty null string and no trailing delimiter");
            }

            if (quote.noQuote()) {
                throw new IllegalArgumentException(
                        "Can't encode an empty value in a single-column CSV without quoting or a trailing delimiter");
            }

            char quoteChar = quote.quoteChar();
            out.append(quoteChar).append(quoteChar);
        } else {
            out.append(buffer);
        }
        out.append(lineBreak);
    }

    private void printHeaderRow(Index header, Appendable out) throws IOException {
        String[] labels = header.toArray();
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                out.append(delimiter);
                encoder.encode(labels[i], out);
            } else {
                encoder.encodeAtLineStart(labels[i], out);
            }
        }
        printRowEnd(out);
    }

    private void printRow(RowProxy row, int width, Appendable out) throws IOException {
        for (int i = 0; i < width; i++) {
            if (i > 0) {
                out.append(delimiter);
                encoder.encode(row.get(i), out);
            } else {
                encoder.encodeAtLineStart(row.get(i), out);
            }
        }
        printRowEnd(out);
    }

    private void printRowEnd(Appendable out) throws IOException {
        if (trailingDelimiter) {
            out.append(delimiter);
        }
        out.append(lineBreak);
    }

    private static String lineBreakString(CsvFormat format) {
        return switch (format.lineBreak()) {
            case CR -> "\r";
            case LF -> "\n";
            case CRLF, AUTO /* RFC4180 */ -> "\r\n";
        };
    }
}
