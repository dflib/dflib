package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_SelectRowsTest {

    @Test
    public void testInts() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(0, 2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 5, "x")
                .expectRow(1, 1, "z");
    }

    @Test
    public void testInts_out_of_range() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> df.selectRows(0, 3).materialize());
    }

    @Test
    public void testIntSeries() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(new IntArraySeries(0, 2));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 5, "x")
                .expectRow(1, 1, "z");
    }

    @Test
    public void testReorder() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(2, 1);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "z")
                .expectRow(1, 9, "y");
    }

    @Test
    public void testSelectRows_duplicate() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(2, 1, 1, 2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "z")
                .expectRow(1, 9, "y")
                .expectRow(1, 9, "y")
                .expectRow(0, 1, "z");
    }

    @Test
    public void testByColumn_Name() {

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20)
                .selectRows("a", (Integer v) -> v > 15);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testSByColumn_Pos() {

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20)
                .selectRows(0, (Integer v) -> v > 15);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testWithBooleanSeries() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(10, 20, 30)
                .selectRows(BooleanSeries.forBooleans(true, false, true));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 10)
                .expectRow(1, 30);
    }

    @Test
    public void testExp_Ge() {

        Condition c = $int("a").ge(20);

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20, 30, 40)
                .selectRows(c);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, 20)
                .expectRow(1, 30)
                .expectRow(2, 40);
    }

    @Test
    public void testExp_EqOr() {

        Condition c = $col("b").eq($col("a")).or($bool("c"));

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "1", false,
                "2", "2", true,
                "4", "5", false).selectRows(c);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", "1", false)
                .expectRow(1, "2", "2", true);
    }

    @Test
    public void testExp_Lt() {

        Condition c = $int("b").multiply($int("c")).lt($double("a").divide($int("d")));

        DataFrame df = DataFrame.newFrame("a", "b", "c", "d").foldByRow(
                1.01, -1, 0, 1,
                60., 4, 8, 2).selectRows(c);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 1.01, -1, 0, 1);
    }
}
