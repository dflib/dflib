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

    static final DataFrame TWO_COL_TEST_DF =  DataFrame.foldByRow("order", "val").of(
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
                .expectRow(0, 10)
                .expectRow(1, 10)
                .expectRow(2, 10)
                .expectRow(3, 10);
    }

    @Test
    public void preceding() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allPreceding).select($int("val").sum());
        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 23)
                .expectRow(2, 38)
                .expectRow(3, 40);
    }

    @Test
    public void preceding_MultiExp() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allPreceding).select(
                $int("val").sum(),
                $int("val").max());

        new DataFrameAsserts(r, "sum(val)", "max(val)").expectHeight(4)
                .expectRow(0, 1, 1)
                .expectRow(1, 23, 22)
                .expectRow(2, 38, 22)
                .expectRow(3, 40, 22);
    }

    @Test
    public void following() {
        DataFrame r = SINGLE_COL_TEST_DF.over().range(WindowRange.allFollowing).select($int("val").sum());
        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 40)
                .expectRow(1, 39)
                .expectRow(2, 17)
                .expectRow(3, 2);
    }

    @Test
    public void preceding_Sorted() {

        DataFrame r = TWO_COL_TEST_DF.over()
                .sorted($col("order").asc())
                .range(WindowRange.allPreceding)
                .select($int("val").sum());

        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 15)
                .expectRow(1, 16)
                .expectRow(2, 18)
                .expectRow(3, 40);
    }

    @Test
    public void following_Sorted() {

        DataFrame r = TWO_COL_TEST_DF.over()
                .sorted($col("order").asc())
                .range(WindowRange.allFollowing)
                .select($int("val").sum());

        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 40)
                .expectRow(1, 25)
                .expectRow(2, 24)
                .expectRow(3, 22);
    }

    @Test
    public void preceding_Partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("label", "order", "val").of(
                "a", 2, 1,
                "a", 3, 22,
                "b", 1, 15,
                "a", 1, 2,
                "c", 1, 77,
                "b", 2, 12);

        DataFrame r = df.over()
                .partitioned("label")
                .sorted($col("order").asc())
                .range(WindowRange.allPreceding)
                .select(
                        $int("val").sum(),
                        $str("label").first());

        new DataFrameAsserts(r, "sum(val)", "first(label)").expectHeight(6)
                .expectRow(0, 3, "a")
                .expectRow(1, 25, "a")
                .expectRow(2, 15, "b")
                .expectRow(3, 2, "a")
                .expectRow(4, 77, "c")
                .expectRow(5, 27, "b");
    }

}
