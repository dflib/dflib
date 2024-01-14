package org.dflib;

import org.dflib.series.IntArraySeries;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Deprecated
public class DataFrame_SelectRowsTest {

    @Test
    public void ints() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void ints_out_of_range() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                5, "x",
                9, "y",
                1, "z");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> df.selectRows(0, 3).materialize());
    }

    @Test
    public void intSeries() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void reorder() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void selectRows_duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void byColumn_Name() {

        DataFrame df = DataFrame.foldByRow("a").of(10, 20, null)
                .selectRows("a", (Integer v) -> v == null || v > 15);

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, null);
    }

    @Test
    public void sByColumn_Pos() {

        DataFrame df = DataFrame.foldByRow("a").of(10, 20)
                .selectRows(0, (Integer v) -> v > 15);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void withBooleanSeries() {

        DataFrame df = DataFrame.foldByRow("a").of(10, 20, 30)
                .selectRows(Series.ofBool(true, false, true));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 10)
                .expectRow(1, 30);
    }

    @Test
    public void exp_Ge() {

        Condition c = $int("a").ge(20);

        DataFrame df = DataFrame.foldByRow("a").of(10, 20, 30, 40)
                .selectRows(c);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, 20)
                .expectRow(1, 30)
                .expectRow(2, 40);
    }

    @Test
    public void exp_EqOr() {

        Condition c = $col("b").eq($col("a")).or($bool("c"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "1", false,
                "2", "2", true,
                "4", "5", false).selectRows(c);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", "1", false)
                .expectRow(1, "2", "2", true);
    }

    @Test
    public void exp_Lt() {

        Condition c = $int("b").mul($int("c")).lt($double("a").div($int("d")));

        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1.01, -1, 0, 1,
                60., 4, 8, 2).selectRows(c);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 1.01, -1, 0, 1);
    }

    @Test
    public void exp_In() {

        Condition c = $col("b").in("x", "a");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "x", false,
                "2", "2", true,
                "4", "a", false).selectRows(c);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", "x", false)
                .expectRow(1, "4", "a", false);
    }

    @Test
    public void exp_NotIn() {

        Condition c = $col("b").notIn("x", "a");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "x", false,
                "2", "2", true,
                "4", "a", false).selectRows(c);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(1)
                .expectRow(0, "2", "2", true);
    }
}
