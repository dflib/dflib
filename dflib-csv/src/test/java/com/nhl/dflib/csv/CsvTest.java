package com.nhl.dflib.csv;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import org.junit.Test;

public class CsvTest extends BaseCsvTest {


    @Test
    public void testFromFile() {
        DataFrame df = Csv.fromFile(csvPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void testLoader_FromFile() {
        DataFrame df = Csv.loader().fromFile(csvPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }
}
