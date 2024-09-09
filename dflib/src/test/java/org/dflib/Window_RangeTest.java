package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.dflib.window.WindowRange;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class Window_RangeTest {

    @Test
    public void all_empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().range(WindowRange.all).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void preceding_empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().range(WindowRange.allPreceding).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void following_empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().range(WindowRange.allFollowing).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over().range(WindowRange.all).select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

    @Test
    public void range_Preceding() {
        DataFrame df = DataFrame.foldByRow("val").of(
                1,
                22,
                15,
                2);

        DataFrame r = df.over().range(WindowRange.allPreceding).select($int("val").sum());
        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 23)
                .expectRow(2, 38)
                .expectRow(3, 40);
    }

    @Test
    public void range_Preceding_MultiAgg() {
        DataFrame df = DataFrame.foldByRow("val").of(
                1,
                22,
                15,
                2);

        DataFrame r = df.over().range(WindowRange.allPreceding).select(
                $int("val").sum(),
                $int("val").max());

        new DataFrameAsserts(r, "sum(val)", "max(val)").expectHeight(4)
                .expectRow(0, 1, 1)
                .expectRow(1, 23, 22)
                .expectRow(2, 38, 22)
                .expectRow(3, 40, 22);
    }

    @Test
    public void range_Following() {
        DataFrame df = DataFrame.foldByRow("val").of(
                1,
                22,
                15,
                2);

        DataFrame r = df.over().range(WindowRange.allFollowing).select($int("val").sum());
        new DataFrameAsserts(r, "sum(val)").expectHeight(4)
                .expectRow(0, 40)
                .expectRow(1, 39)
                .expectRow(2, 17)
                .expectRow(3, 2);
    }

    @Test
    public void sorted_Range_Preceding() {
        DataFrame df = DataFrame.foldByRow("order", "val").of(
                2, 1,
                4, 22,
                1, 15,
                3, 2);

        DataFrame r = df.over()
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
    public void sorted_Range_Following() {
        DataFrame df = DataFrame.foldByRow("order", "val").of(
                2, 1,
                4, 22,
                1, 15,
                3, 2);

        DataFrame r = df.over()
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
    public void partitioned_Sorted_Range_Preceding() {
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

        new DataFrameAsserts(r, "sum(val)", "label").expectHeight(6)
                .expectRow(0, 3, "a")
                .expectRow(1, 25, "a")
                .expectRow(2, 15, "b")
                .expectRow(3, 2, "a")
                .expectRow(4, 77, "c")
                .expectRow(5, 27, "b");
    }

}
