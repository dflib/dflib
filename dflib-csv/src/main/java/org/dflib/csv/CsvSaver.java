package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.dflib.DataFrame;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.printer.CsvPrinter;
import org.dflib.csv.printer.CsvPrinterConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;

public class CsvSaver {

    private final CsvPrinterConfig.Builder configBuilder = CsvPrinterConfig.builder();
    private boolean createMissingDirs;

    /**
     * Sets a compression codec for this saver. If not set, the saver will try to determine compression preferences
     * using the target file extension. So this method is especially useful if the target is not a file. Ignored when
     * saving to character-based sinks. E.g., {@link #save(DataFrame, Appendable)}.
     *
     * @since 2.0.0
     */
    public CsvSaver compression(Codec codec) {
        configBuilder.compressionCodec(codec);
        return this;
    }

    /**
     * Optionally sets the style or format of the imported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * @param format a format object defined in commons-csv library
     * @return this saver instance
     * @deprecated since 2.0.0, use {@link #format(CsvFormat)} instead
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public CsvSaver format(CSVFormat format) {
        CsvFormat.Builder b = CsvFormat.defaultFormat();
        b.copyFrom(format);
        configBuilder.csvFormat(b.build());
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
        configBuilder.csvFormat(format);
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
        configBuilder.printHeader(false);
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
            newPrinter(file.getName()).write(df, out);
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
        newPrinter(null).write(df, out);
    }

    /**
     * Saves a CSV to an Appendable. Ignores compression settings. If compression is essential for in-memory saving,
     * use {@link #save(DataFrame, OutputStream)}.
     */
    public void save(DataFrame df, Appendable out) {
        newPrinter(null).write(df, out);
    }

    public String saveToString(DataFrame df) {
        // producing a String, so no compression by definition
        StringWriter out = new StringWriter();
        save(df, out);
        return out.toString();
    }

    private CsvPrinter newPrinter(String fileNameForCodecDetection) {
        Codec userCodec = configBuilder.compressionCodec();

        // apply the (possibly detected) codec only for this build, then restore the user's value
        // so a subsequent save with a different filename re-detects from scratch
        Optional<Codec> codecFromFileName = Codec.ofUri(fileNameForCodecDetection);
        codecFromFileName.ifPresent(configBuilder::compressionCodec);
        CsvPrinterConfig config = configBuilder.build();
        configBuilder.compressionCodec(userCodec);

        return new CsvPrinter(config);
    }
}
