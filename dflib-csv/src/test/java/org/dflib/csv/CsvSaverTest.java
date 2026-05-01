package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaverTest {

    private static final CsvFormat FORMAT = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();
    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @Test
    public void saveToString_Format() {
        CsvFormat format = FORMAT.builder().delimiter("\t").build();
        assertEquals("A\tB\n1\t2\n3\t4\n", Csv.saver().format(format).saveToString(DF));
    }

    @Test
    public void saveToString_NoHeader() {
        assertEquals("1,2\n3,4\n", Csv.saver().format(FORMAT).noHeader().saveToString(DF));
    }
}
