package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.dflib.window.WindowRange;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_SelectRangeTest {

    static final DataFrame EMPTY_TEST_DF = DataFrame.empty("a", "b", "c");

    static final DataFrame SINGLE_COL_TEST_DF = DataFrame.foldByRow("val").of(
            1,
            22,
            15,
            2);

    static final DataFrame TWO_COL_TEST_DF = DataFrame.foldByRow("order", "val").of(
            2, 1,
            4, 22,
            1, 15,
            3, 2);

    @Test
    public void all_Empty() {
        DataFrame r = EMPTY_TEST_DF.over().range(WindowRange.all).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void preceding_Empty() {
        DataFrame r = EMPTY_TEST_DF.over().range(WindowRange.allPreceding).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void following_Empty() {
        DataFrame r = EMPTY_TEST_DF.over().range(WindowRange.allFollowing).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void all() {

        DataFrame r = TWO_COL_TEST_DF.over().range(WindowRange.all).select($int(0).sum());
        new DataFrameAsserts(r, "sum(order)")
                .expectHeight(4)
                .expectRow(0, 10L)
                .expectRow(1, 10L)
                .expectRow(2, 10L)
                .expectRow(3, 10L);
    }

    @Test
    public void preceding() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allPreceding).select($int("val").sum());
        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 1L)
                .expectRow(1, 23L)
                .expectRow(2, 38L)
                .expectRow(3, 40L);
    }

    @Test
    public void preceding_MultiExp() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allPreceding).select(
                $int("val").sum(), // aggregating
                $int("val").max(), // aggregating
                rowNum());  // non-aggregating

        new DataFrameAsserts(r, "sum(val)", "max(val)", "rowNum()")
                .expectHeight(4)
                .expectRow(0, 1L, 1, 1)
                .expectRow(1, 23L, 22, 2)
                .expectRow(2, 38L, 22, 3)
                .expectRow(3, 40L, 22, 4);
    }

    @Test
    public void following() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allFollowing).select($int("val").sum());
        new DataFrameAsserts(r, "sum(val)")
                .expectHeight(4)
                .expectRow(0, 40L)
                .expectRow(1, 39L)
                .expectRow(2, 17L)
                .expectRow(3, 2L);
    }

    @Test
    public void following_MultiExp() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allFollowing).select(
                $col("val"), // non-aggregating
                $int("val").sum(), // aggregating
                $int("val").max(), // aggregating
                rowNum());  // non-aggregating

        new DataFrameAsserts(r, "val", "sum(val)", "max(val)", "rowNum()")
                .expectHeight(4)
                .expectRow(0, 1, 40L, 22, 1)
                .expectRow(1, 22, 39L, 22, 1)
                .expectRow(2, 15, 17L, 15, 1)
                .expectRow(3, 2, 2L, 2, 1);
    }

    @Test
    public void preceding_sort() {

        DataFrame r = TWO_COL_TEST_DF.over()
                .sort($col("order").asc())
                .range(WindowRange.allPreceding)
                .select($int("val").sum());

        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 15L)
                .expectRow(1, 16L)
                .expectRow(2, 18L)
                .expectRow(3, 40L);
    }

    @Test
    public void following_sort() {

        DataFrame r = TWO_COL_TEST_DF.over()
                .sort($col("order").asc())
                .range(WindowRange.allFollowing)
                .select($int("val").sum());

        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 40L)
                .expectRow(1, 25L)
                .expectRow(2, 24L)
                .expectRow(3, 22L);
    }

    @Test
    public void preceding_partition_sort() {
        DataFrame df = DataFrame.foldByRow("label", "order", "val").of(
                "a", 2, 1,
                "a", 3, 22,
                "b", 1, 15,
                "a", 1, 2,
                "c", 1, 77,
                "b", 2, 12);

        DataFrame r = df.over()
                .partition("label")
                .sort($col("order").asc())
                .range(WindowRange.allPreceding)
                .select(
                        $int("val").sum(),
                        $str("label").first());

        new DataFrameAsserts(r, "sum(val)", "first(label)").expectHeight(6)
                .expectRow(0, 3L, "a")
                .expectRow(1, 25L, "a")
                .expectRow(2, 15L, "b")
                .expectRow(3, 2L, "a")
                .expectRow(4, 77L, "c")
                .expectRow(5, 27L, "b");
    }

}
