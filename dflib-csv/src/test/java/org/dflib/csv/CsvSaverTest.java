package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.printer.CsvPrinter;
import org.dflib.csv.printer.CsvPrinterFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvSaverTest {

    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @Test
    public void saveToString_Format() {
        CsvFormat format = CsvFormat.defaultFormat().delimiter("\t").build();
        assertEquals("A\tB\r\n1\t2\r\n3\t4\r\n", Csv.saver().format(format).saveToString(DF));
    }

    @Deprecated
    @Test
    public void saveToString_CommonsFormat() {
        assertEquals("A\tB\n1\t2\n3\t4\n", Csv.saver().format(CSVFormat.MYSQL).saveToString(DF));
    }

    @Deprecated
    @Test
    public void saveToString_CommonsFormat_QuoteAll() {
        CSVFormat format = CSVFormat.DEFAULT.builder().setQuoteMode(QuoteMode.ALL).build();
        assertEquals("\"A\",\"B\"\r\n\"1\",\"2\"\r\n\"3\",\"4\"\r\n",
                Csv.saver().format(format).saveToString(DF));
    }

    @Test
    public void saveToString_NoHeader() {
        assertEquals("1,2\r\n3,4\r\n", Csv.saver().noHeader().saveToString(DF));
    }

    @Test
    public void printerFactory() {
        CsvPrinterFactory factory = config -> new CsvPrinter() {
            @Override
            public void write(DataFrame df, Appendable out) {
                try {
                    out.append("rows:").append(String.valueOf(df.height()))
                            .append(",header:").append(String.valueOf(config.printHeader()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void write(DataFrame df, OutputStream out) {
                throw new UnsupportedOperationException();
            }
        };

        assertEquals("rows:2,header:false", Csv.saver().printerFactory(factory).noHeader().saveToString(DF));
    }

    @Test
    public void printerFactory_Null() {
        assertThrows(NullPointerException.class, () -> Csv.saver().printerFactory(null));
        assertThrows(NullPointerException.class, () -> Csv.saver().printerFactory(c -> null).saveToString(DF));
    }
}
