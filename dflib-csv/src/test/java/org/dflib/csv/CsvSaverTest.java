package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaverTest {

    @Test
    public void saveToString_Format() {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        assertEquals("A\tB\n" +
                        "1\t2\n" +
                        "3\t4\n",
                Csv.saver().format(CSVFormat.MYSQL).saveToString(df));
    }

    @Test
    public void saveToString_NoHeader() {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        assertEquals(
                "1,2\r\n" +
                        "3,4\r\n",
                Csv.saver().noHeader().saveToString(df));
    }
}
