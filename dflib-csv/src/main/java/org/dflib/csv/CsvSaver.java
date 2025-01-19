package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.codec.Codec;
import org.dflib.row.RowProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;

public class CsvSaver {

    private CSVFormat format;
    private boolean createMissingDirs;
    private boolean printHeader;
    private Codec codec;

    public CsvSaver() {
        this.format = CSVFormat.DEFAULT;
        this.printHeader = true;
    }

    /**
     * Sets a compression codec for this saver. If not set, the saver will try to determine compression preferences
     * using the target file extension. So this method is especially useful if the target is not a file.
     *
     * @since 2.0.0
     */
    public CsvSaver compression(Codec codec) {
        this.codec = codec;
        return this;
    }

    /**
     * Optionally sets the style or format of the imported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * @param format a format object defined in commons-csv library
     * @return this saver instance
     */
    public CsvSaver format(CSVFormat format) {
        this.format = format;
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

        try (Writer out = new OutputStreamWriter(compressIfNeeded(new FileOutputStream(file), file.getName()))) {
            doSave(df, out);
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

    public void save(DataFrame df, Appendable out) {
        try {
            // producing a char stream, so no compression by definition
            doSave(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV: " + e.getMessage(), e);
        }
    }

    public String saveToString(DataFrame df) {
        // producing a String, so no compression by definition
        StringWriter out = new StringWriter();
        save(df, out);
        return out.toString();
    }

    private void doSave(DataFrame df, Appendable out) throws IOException {
        CSVPrinter printer = new CSVPrinter(out, format);
        if (printHeader) {
            printHeader(printer, df.getColumnsIndex());
        }

        int len = df.width();
        for (RowProxy r : df) {
            printRow(printer, r, len);
        }
    }

    private OutputStream compressIfNeeded(OutputStream out, String fileName) throws IOException {
        Codec codec = this.codec != null ? this.codec : Codec.ofUri(fileName).orElse(null);
        return codec != null ? codec.compress(out) : out;
    }

    private void printHeader(CSVPrinter printer, Index index) throws IOException {
        for (String label : index) {
            printer.print(label);
        }
        printer.println();
    }

    private void printRow(CSVPrinter printer, RowProxy row, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            printer.print(row.get(i));
        }
        printer.println();
    }
}
