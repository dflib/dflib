package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.dflib.DataFrame;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Quote;
import org.dflib.csv.printer.CsvPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;

public class CsvSaver {

    private CsvFormat format;
    private Charset encoding;
    private boolean printHeader;
    private Codec codec;
    private boolean createMissingDirs;

    public CsvSaver() {
        this.format = CsvFormat.defaultFormat().build();
        this.encoding = Charset.defaultCharset();
        this.printHeader = true;
    }

    /**
     * Sets a compression codec for this saver. If not set, the saver will try to determine compression preferences
     * using the target file extension. So this method is especially useful if the target is not a file. Ignored when
     * saving to character-based sinks. E.g., {@link #save(DataFrame, Appendable)}.
     *
     * @since 2.0.0
     */
    public CsvSaver compression(Codec codec) {
        this.codec = codec;
        return this;
    }

    /**
     * @since 2.0.0
     */
    public CsvSaver encoding(String encoding) {
        return encoding(encoding != null ? Charset.forName(encoding) : null);
    }

    /**
     * Allows changing the encoding from the platform default. Passing {@code null} resets it to the platform default.
     * Ignored when saving to character-based sinks. E.g., {@link #save(DataFrame, Appendable)}.
     *
     * @since 2.0.0
     */
    public CsvSaver encoding(Charset encoding) {
        this.encoding = encoding != null ? encoding : Charset.defaultCharset();
        return this;
    }

    /**
     * Optionally sets the style or format of the exported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * <p>The format is converted to {@link CsvFormat} on a best-effort basis, and the output is not guaranteed to
     * be the same as the one produced by "commons-csv". Only the delimiter, quote and escape chars, null string,
     * comment prefix, trailing delimiter, trimming, and CR, LF or CRLF line separators are taken from the format.
     * The values are never trimmed on save. Instead, if trimming is enabled in the format, the values with leading
     * or trailing whitespace are quoted to preserve it.
     * {@link QuoteMode#ALL} and {@link QuoteMode#ALL_NON_NULL} are mapped to mandatory quoting,
     * other quote modes, including {@link QuoteMode#NON_NUMERIC}, to minimal quoting.
     *
     * @param format a format object defined in commons-csv library
     * @return this saver instance
     * @deprecated since 2.0.0, use {@link #format(CsvFormat)} instead
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public CsvSaver format(CSVFormat format) {
        CsvFormat.Builder b = CsvFormat.defaultFormat();
        b.copyFrom(format);

        Character quoteChar = format.getQuoteCharacter();
        QuoteMode quoteMode = format.getQuoteMode();
        if (quoteChar != null && (quoteMode == QuoteMode.ALL || quoteMode == QuoteMode.ALL_NON_NULL)) {
            b.quote(Quote.of(quoteChar));
        }

        this.format = b.build();
        return this;
    }

    /**
     * Optionally sets the style or format of the exported CSV. Allows customizing the format by defining custom
     * delimiters, line separators, quoting and escaping rules, etc.
     *
     * @param format CSV format to use
     * @return this saver instance
     *
     * @see CsvFormat#defaultFormat()
     *
     * @since 2.0.0
     */
    public CsvSaver format(CsvFormat format) {
        this.format = Objects.requireNonNull(format);
        return this;
    }

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     */
    public CsvSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    /**
     * Instructs the saver to omit saving the Index of a DataFrame. By default, the Index will be saved as a first row
     * in a file.
     *
     * @return this saver instance
     */
    public CsvSaver noHeader() {
        this.printHeader = false;
        return this;
    }

    public void save(DataFrame df, File file) {

        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }

        try (OutputStream out = new FileOutputStream(file)) {
            Codec codec = this.codec != null ? this.codec : Codec.ofUri(file.getName()).orElse(null);
            doSave(df, out, codec);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV to " + file + ": " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, Path filePath) {
        save(df, filePath.toFile());
    }

    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    /**
     * @since 2.0.0
     */
    public void save(DataFrame df, OutputStream out) {
        // the stream is closed even if it can't be wrapped in a compressing stream
        try (out) {
            doSave(df, out, codec);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a CSV to an Appendable. Ignores compression settings. If compression is essential for in-memory saving,
     * use {@link #save(DataFrame, OutputStream)}.
     */
    public void save(DataFrame df, Appendable out) {
        try {
            new CsvPrinter(format, printHeader).print(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV: " + e.getMessage(), e);
        }
    }

    public String saveToString(DataFrame df) {
        // producing a String, so no compression by definition
        StringBuilder out = new StringBuilder();
        save(df, out);
        return out.toString();
    }

    private void doSave(DataFrame df, OutputStream out, Codec codec) throws IOException {
        OutputStream compressed = codec != null ? codec.compress(out) : out;
        try (Writer w = new BufferedWriter(new OutputStreamWriter(compressed, encoding))) {
            new CsvPrinter(format, printHeader).print(df, w);
        }
    }
}
