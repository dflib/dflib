package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

import java.io.StringReader;
import java.util.Random;

public class CsvLoader_FilterTest {

    private String csv() {
        return "A,B" + System.lineSeparator()
                + "1,7" + System.lineSeparator()
                + "2,8" + System.lineSeparator()
                + "3,9" + System.lineSeparator()
                + "4,10" + System.lineSeparator()
                + "5,11" + System.lineSeparator()
                + "6,12" + System.lineSeparator();
    }

    @Test
    public void testFilterRows_Pos() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .filterRows(0, (Integer i) -> i % 2 == 0)
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void testFilterRows_Name() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .filterRows("A", (Integer i) -> i % 2 == 0)
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void testFilterRows_MultipleFilters() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .intColumn(1)
                .filterRows("A", (Integer i) -> i % 2 == 0)
                .filterRows("A", (Integer i) -> i > 2)
                .filterRows("B", (Integer i) -> i == 12)
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(1)
                .expectRow(0, 6, 12);
    }

    @Test
    public void testFilterRows_WithSampling() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .filterRows("A", (Integer i) -> i > 1)
                .sampleRows(2, new Random(9))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 2, "8")
                .expectRow(1, 5, "11");
    }

    @Test
    public void testFilterRows_WithSampling_SmallerThanSampleSize() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .filterRows("A", (Integer i) -> i % 2 == 0)
                .sampleRows(4, new Random(8))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void testFilterRows_SelectColumns() {

        DataFrame df = new CsvLoader()
                .intColumn("A")
                .intColumn("B")
                .filterRows("A", (Integer i) -> i % 2 == 0)
                .selectColumns("B")
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "B")
                .expectHeight(3)
                .expectRow(0, "8")
                .expectRow(1,"10")
                .expectRow(2, "12");
    }

}
