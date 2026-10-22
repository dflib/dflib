package org.dflib.csv.printer;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Quote;
import org.dflib.row.RowProxy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * CSV writer main entry point, orchestrates the whole writing process.
 */
class DefaultCsvPrinter implements CsvPrinter {

    private final CsvPrinterConfig config;
    private final DefaultFieldEncoder encoder;

    /**
     * Creates a printer using the given configuration.
     *
     * @param config writer configuration
     */
    public DefaultCsvPrinter(CsvPrinterConfig config) {
        this.config = Objects.requireNonNull(config);
        this.encoder = new DefaultFieldEncoder(config.csvFormat());
    }

    @Override
    public void write(DataFrame df, Appendable out) {
        try {
            doWrite(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV: " + e.getMessage(), e);
        }
    }

    @Override
    public void write(DataFrame df, OutputStream out) {
        Codec codec = config.compressionCodec();
        try {
            OutputStream compressed = codec != null ? codec.compress(out) : out;
            try (Writer w = new BufferedWriter(new OutputStreamWriter(compressed, config.encoding()))) {
                write(df, w);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV: " + e.getMessage(), e);
        }
    }

    private void doWrite(DataFrame df, Appendable out) throws IOException {
        CsvFormat fmt = config.csvFormat();
        String delimiter = new String(fmt.delimiter().asArray());
        String lineBreak = lineBreakString(fmt);
        boolean trailingDelimiter = fmt.trailingDelimiter();

        int width = df.width();
        if (width == 1 && !trailingDelimiter) {
            writeSingleColumn(df, out, lineBreak, fmt.quote());
            return;
        }

        if (config.printHeader()) {
            writeHeader(df.getColumnsIndex(), out, delimiter, lineBreak, trailingDelimiter);
        }

        for (RowProxy row : df) {
            writeRow(row, width, out, delimiter, lineBreak, trailingDelimiter);
        }
    }

    // single column records must never be empty lines, so each value is encoded to a buffer first to check for that
    private void writeSingleColumn(DataFrame df, Appendable out, String lineBreak, Quote quote) throws IOException {
        StringBuilder buffer = new StringBuilder();

        if (config.printHeader()) {
            writeSingleValue(df.getColumnsIndex().get(0), out, buffer, lineBreak, quote);
        }

        for (Object value : df.getColumn(0)) {
            writeSingleValue(value, out, buffer, lineBreak, quote);
        }
    }

    private void writeSingleValue(Object value, Appendable out, StringBuilder buffer, String lineBreak, Quote quote)
            throws IOException {

        buffer.setLength(0);
        encoder.encodeAtLineStart(value, buffer);
        if (buffer.isEmpty()) {
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

    private void writeHeader(Index header, Appendable out,
                             String delimiter, String lineBreak, boolean trailingDelimiter) throws IOException {
        String[] labels = header.toArray();
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                out.append(delimiter);
                encoder.encode(labels[i], out);
            } else {
                encoder.encodeAtLineStart(labels[i], out);
            }
        }
        if (trailingDelimiter) {
            out.append(delimiter);
        }
        out.append(lineBreak);
    }

    private void writeRow(RowProxy row, int width, Appendable out,
                          String delimiter, String lineBreak, boolean trailingDelimiter) throws IOException {
        for (int i = 0; i < width; i++) {
            if (i > 0) {
                out.append(delimiter);
                encoder.encode(row.get(i), out);
            } else {
                encoder.encodeAtLineStart(row.get(i), out);
            }
        }
        if (trailingDelimiter) {
            out.append(delimiter);
        }
        out.append(lineBreak);
    }

    private static String lineBreakString(CsvFormat fmt) {
        return switch (fmt.lineBreak()) {
            case CR -> "\r";
            case LF -> "\n";
            case CRLF, AUTO /* RFC4180 */ -> "\r\n";
        };
    }
}
