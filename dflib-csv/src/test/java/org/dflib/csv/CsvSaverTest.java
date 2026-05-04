package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.printer.DefaultCsvPrinter;
import org.dflib.csv.printer.CsvPrinterFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaverTest {

    private static final CsvFormat FORMAT = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();
    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @Test
    public void saveToString_Format() {
        CsvFormat format = CsvFormat.defaultFormat().copyFrom(FORMAT).delimiter("\t").build();
        assertEquals("A\tB\n1\t2\n3\t4\n", Csv.saver().format(format).saveToString(DF));
    }

    @Test
    public void saveToString_NoHeader() {
        assertEquals("1,2\n3,4\n", Csv.saver().format(FORMAT).noHeader().saveToString(DF));
    }

    @Test
    public void saveToString_CustomPrinterFactory() {
        CsvPrinterFactory factory = config -> new DefaultCsvPrinter(config, (value, out) -> out.append('x'));

        assertEquals("x,x\nx,x\nx,x\n", Csv.saver().printerFactory(factory).format(FORMAT).saveToString(DF));
    }
}
