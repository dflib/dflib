package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

import java.io.StringReader;
import java.util.Random;

public class CsvLoader_SamplingTest {

    @Test
    public void testSampleRows() {

        String csv = "A,B" + System.lineSeparator()
                + "1,2" + System.lineSeparator()
                + "3,4" + System.lineSeparator()
                + "5,6" + System.lineSeparator()
                + "7,8" + System.lineSeparator()
                + "9,10" + System.lineSeparator()
                + "11,12" + System.lineSeparator();

        // using fixed Random seed to get reproducible result
        DataFrame df1 = new CsvLoader()
                .sampleRows(2, new Random(8))
                .load(new StringReader(csv));

        new DataFrameAsserts(df1, "A", "B")
                .expectHeight(2)
                .expectRow(0, "9", "10")
                .expectRow(1, "11", "12");

        // do another test with different random seed
        DataFrame df2 = new CsvLoader()
                .sampleRows(2, new Random(15))
                .load(new StringReader(csv));

        new DataFrameAsserts(df2, "A", "B")
                .expectHeight(2)
                .expectRow(0, "3", "4")
                .expectRow(1, "5", "6");

        // and one more test
        DataFrame df3 = new CsvLoader()
                .sampleRows(3, new Random(3))
                .load(new StringReader(csv));

        new DataFrameAsserts(df3, "A", "B")
                .expectHeight(3)
                .expectRow(0, "3", "4")
                .expectRow(1, "7", "8")
                .expectRow(2, "11", "12");
    }
}
