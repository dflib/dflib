package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;
import static org.dflib.Exp.*;

public class Window_DenseRankTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        IntSeries r = df.over().denseRank();
        new IntSeriesAsserts(r).expectData();
    }

    @Test
    public void test() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries r = df.over().denseRank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(r).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void partition() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rna = df.over().partition("a").denseRank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rna).expectData(1, 1, 1, 1, 1);

        IntSeries rnb = df.over().partition("b").denseRank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rnb).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void sort() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "v",
                1, "z", null,
                0, "a", null,
                1, "x", "m");

        IntSeries rn1 = df.over().sort("a", true).denseRank();
        new IntSeriesAsserts(rn1).expectData(2, 3, 2, 1, 2);

        IntSeries rn2 = df.over().sort("b", true).denseRank();
        new IntSeriesAsserts(rn2).expectData(2, 3, 4, 1, 2);

        IntSeries rn3 = df.over().sort("c", true).denseRank();
        new IntSeriesAsserts(rn3).expectData(1, 2, 3, 3, 1);
    }

    @Test
    public void partition_sort() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn = df.over().partition("a").sort("b", true).denseRank();
        new IntSeriesAsserts(rn).expectData(1, 1, 2, 1, 1);
    }

    @Test
    public void sort_Sorter() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "v",
                1, "z", null,
                0, "a", null,
                1, "x", "m");

        // a case of missing sorter - all rows are considered peers
        IntSeries rn0 = df.over().sort().denseRank();
        new IntSeriesAsserts(rn0).expectData(1, 1, 1, 1, 1);

        IntSeries rn1 = df.over().sort($col("a").asc()).denseRank();
        new IntSeriesAsserts(rn1).expectData(2, 3, 2, 1, 2);

        IntSeries rn2 = df.over().sort($col("b").asc()).denseRank();
        new IntSeriesAsserts(rn2).expectData(2, 3, 4, 1, 2);

        IntSeries rn3 = df.over().sort($col("c").asc()).denseRank();
        new IntSeriesAsserts(rn3).expectData(1, 2, 3, 3, 1);
    }

    @Test
    public void partition_sort_Sorter() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        // a case of missing sorter - all rows are considered peers
        IntSeries rn0 = df.over().partition("a").sort().denseRank();
        new IntSeriesAsserts(rn0).expectData(1, 1, 1, 1, 1);

        IntSeries rn1 = df.over().partition("a").sort($col("b").asc()).denseRank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 2, 1, 1);
    }

}
