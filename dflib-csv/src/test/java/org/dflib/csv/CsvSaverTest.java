package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
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

    @Deprecated
    @Test
    public void saveToString_CommonsFormat_NoRecordSeparator() {
        assertEquals("A;B\r\n1;2\r\n3;4\r\n", Csv.saver().format(CSVFormat.newFormat(';')).saveToString(DF));
    }

    @Test
    public void saveToString_NoHeader() {
        assertEquals("1,2\r\n3,4\r\n", Csv.saver().noHeader().saveToString(DF));
    }
}
