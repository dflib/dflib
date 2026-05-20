package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.printer.DefaultCsvPrinter;
import org.dflib.csv.printer.CsvPrinterFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaverTest {

    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @Test
    public void saveToString_Format() {
        CsvFormat format = CsvFormat.defaultFormat().delimiter("\t").build();
        assertEquals("A\tB\r\n1\t2\r\n3\t4\r\n", Csv.saver().format(format).saveToString(DF));
    }

    @Test
    public void saveToString_NoHeader() {
        assertEquals("1,2\r\n3,4\r\n", Csv.saver().noHeader().saveToString(DF));
    }

    @Test
    public void saveToString_CustomPrinterFactory() {
        CsvPrinterFactory factory = config -> new DefaultCsvPrinter(config, (value, out) -> out.append('x'));

        assertEquals("x,x\r\nx,x\r\nx,x\r\n", Csv.saver().printerFactory(factory).saveToString(DF));
    }
}
