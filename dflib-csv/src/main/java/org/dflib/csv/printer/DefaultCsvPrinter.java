package org.dflib.csv.printer;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.row.RowProxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * CSV writer main entry point, orchestrates the whole writing process.
 * <br>
 * Internal API. Part of the {@link org.dflib.csv.CsvSaver} API.
 *
 * @since 2.0.0
 */
public class DefaultCsvPrinter implements CsvPrinter {

    final CsvPrinterConfig config;
    private final CsvFieldEncoder encoder;

    /**
     * Creates a printer with default configuration.
     */
    public DefaultCsvPrinter() {
        this(CsvPrinterConfig.builder().build());
    }

    /**
     * Creates a printer using the given configuration.
     *
     * @param config writer configuration
     */
    public DefaultCsvPrinter(CsvPrinterConfig config) {
        this(config, new DefaultFieldEncoder(config.csvFormat()));
    }

    /**
     * Creates a printer using the given configuration and field encoder.
     *
     * @param config writer configuration
     * @param encoder field encoder strategy
     */
    public DefaultCsvPrinter(CsvPrinterConfig config, CsvFieldEncoder encoder) {
        this.config = Objects.requireNonNull(config);
        this.encoder = Objects.requireNonNull(encoder);
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
            try (Writer w = new OutputStreamWriter(compressed, config.encoding())) {
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

        if (config.printHeader()) {
            writeHeader(df.getColumnsIndex(), out, delimiter, lineBreak, trailingDelimiter);
        }

        int width = df.width();
        for (RowProxy row : df) {
            writeRow(row, width, out, delimiter, lineBreak, trailingDelimiter);
        }
    }

    private void writeHeader(Index header, Appendable out,
                             String delimiter, String lineBreak, boolean trailingDelimiter) throws IOException {
        String[] labels = header.toArray();
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                out.append(delimiter);
            }
            encoder.encode(labels[i], out);
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
            }
            encoder.encode(row.get(i), out);
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
            case AUTO, CRLF -> "\r\n";
        };
    }
}
