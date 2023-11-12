package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvLoader_SelectColumnsTest {

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
    public void pos() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .selectRows(RowPredicate.of(0, (Integer i) -> i % 2 == 0))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void name() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .selectRows(RowPredicate.of("A", (Integer i) -> i % 2 == 0))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void multipleConditions_LastWins() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .intColumn(1)
                .selectRows(RowPredicate.of("B", (Integer i) -> i % 2 == 0))
                .selectRows(RowPredicate.of("B", (Integer i) -> i == 12))

                // this is the only one that will have effect
                .selectRows(RowPredicate.of("B", (Integer i) -> i > 10))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 5, 11)
                .expectRow(1, 6, 12);
    }

    @Test
    public void testWithSampling() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .selectRows(RowPredicate.of("A", (Integer i) -> i > 1))
                .sampleRows(2, new Random(9))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 2, "8")
                .expectRow(1, 5, "11");
    }

    @Test
    public void testWithSampling_SmallerThanSampleSize() {

        DataFrame df = new CsvLoader()
                .intColumn(0)
                .selectRows(RowPredicate.of("A", (Integer i) -> i % 2 == 0))
                .sampleRows(4, new Random(8))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void cantFilterOnExcludedColumns() {

        CsvLoader loader = new CsvLoader()
                .intColumn("A")
                .intColumn("B")

                // column "A" is not present in the result, so it should cause an exception on load
                .selectRows(RowPredicate.of("A", (Integer i) -> i % 2 == 0))
                .selectColumns("B");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> loader.load(new StringReader(csv())));
    }

    @Test
    public void selectColumns_Condition() {

        DataFrame df = new CsvLoader()
                .intColumn("A")
                .intColumn("B")
                .selectColumns("B", "A")

                // using positional indices of the resulting DataFrame, not the CSV
                .selectRows(RowPredicate.of(1, (Integer i) -> i == 4))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "B", "A")
                .expectHeight(1)
                .expectRow(0, 10, 4);
    }
}
