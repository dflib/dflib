package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.row.RowProxy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CsvSaver {

    private CSVFormat format;

    public CsvSaver() {
        this.format = CSVFormat.DEFAULT;
    }

    /**
     * Optionally sets the style or format of the imported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * @param format a format object defined in commons-csv library
     * @return this loader instance
     */
    public CsvSaver format(CSVFormat format) {
        this.format = format;
        return this;
    }

    public void toFile(DataFrame df, File file) {

        try (FileWriter out = new FileWriter(file)) {
            toWriter(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV to " + file, e);
        }
    }

    public void toFile(DataFrame df, String fileName) {

        try (FileWriter out = new FileWriter(fileName)) {
            toWriter(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV to " + fileName, e);
        }
    }

    public void toWriter(DataFrame df, Writer out) {

        try {
            CSVPrinter printer = new CSVPrinter(out, format);
            printHeader(printer, df.getColumns());

            int len = df.width();
            for (RowProxy r : df) {
                printRow(printer, r, len);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV", e);
        }
    }

    private void printHeader(CSVPrinter printer, Index index) throws IOException {
        for (IndexPosition p : index.getPositions()) {
            printer.print(p.name());
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
