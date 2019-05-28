package com.nhl.dflib;

import com.nhl.dflib.map.IntValueMapper;
import com.nhl.dflib.map.ValueMapper;
import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataFrame_ConvertTest extends BaseDataFrameTest {

    @Test
    public void testConvertColumn() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .convertColumn("a", v -> ((int) v) * 10);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_ValueMapperToDate() {
        Index i1 = Index.forLabels("a");
        DataFrame df = createDf(i1,
                "2018-01-05",
                "2019-02-28",
                null)
                .convertColumn("a", ValueMapper.stringToDate());

        new DFAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, new Object[]{null});
    }

    @Test
    public void testConvertColumn_ValueMapperToDate_Formatter() {
        Index i1 = Index.forLabels("a");
        DataFrame df = createDf(i1,
                "2018 01 05",
                "2019 02 28",
                null)
                .convertColumn("a", ValueMapper.stringToDate(DateTimeFormatter.ofPattern("yyyy MM dd")));

        new DFAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, new Object[]{null});
    }

    @Test
    public void testConvertColumn_ValueMapperToDateTime() {
        Index i1 = Index.forLabels("a");
        DataFrame df = createDf(i1,
                "2018-01-05T00:01:15",
                "2019-02-28T13:11:12",
                null)
                .convertColumn("a", ValueMapper.stringToDateTime());

        new DFAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDateTime.of(2018, 1, 5, 0, 1, 15))
                .expectRow(1, LocalDateTime.of(2019, 2, 28, 13, 11, 12))
                .expectRow(2, new Object[]{null});
    }

    @Test
    public void testConvertColumnToInt_ByLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                "1", "x",
                "5", "z",
                "2", "y")
                .toIntColumn("a", IntValueMapper.fromString());


        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 5, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void testConvertColumnToInt_ByPos() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                "1", "x",
                "5", "z",
                "2", "y")
                .toIntColumn(0, -1);


        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 5, "z")
                .expectRow(2, 2, "y");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertColumnToInt_Nulls() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                "1", "x",
                null, "z",
                "2", "y")
                .toIntColumn(0, IntValueMapper.fromString());
    }

    @Test
    public void testConvertColumnToInt_NullsDefault() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                "1", "x",
                null, "z",
                "2", "y")
                .toIntColumn(0, -100);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, -100, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void testToEnumFromNumColumn() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                null, "z",
                0, "y")
                .toEnumFromNumColumn(0, X.class);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, X.b, "x")
                .expectRow(1, null, "z")
                .expectRow(2, X.a, "y");
    }

    @Test
    public void testToEnumFromStringColumn() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                "b", "x",
                null, "z",
                "a", "y")
                .toEnumFromStringColumn(0, X.class);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, X.b, "x")
                .expectRow(1, null, "z")
                .expectRow(2, X.a, "y");
    }

    enum X {a, b, c, d, e}
}

