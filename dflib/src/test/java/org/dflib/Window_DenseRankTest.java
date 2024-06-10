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
    public void partitioned() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rna = df.over().partitioned("a").denseRank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rna).expectData(1, 1, 1, 1, 1);

        IntSeries rnb = df.over().partitioned("b").denseRank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rnb).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "v",
                1, "z", null,
                0, "a", null,
                1, "x", "m");

        IntSeries rn1 = df.over().sorted("a", true).denseRank();
        new IntSeriesAsserts(rn1).expectData(2, 3, 2, 1, 2);

        IntSeries rn2 = df.over().sorted("b", true).denseRank();
        new IntSeriesAsserts(rn2).expectData(2, 3, 4, 1, 2);

        IntSeries rn3 = df.over().sorted("c", true).denseRank();
        new IntSeriesAsserts(rn3).expectData(1, 2, 3, 3, 1);
    }

    @Test
    public void partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn = df.over().partitioned("a").sorted("b", true).denseRank();
        new IntSeriesAsserts(rn).expectData(1, 1, 2, 1, 1);
    }

    @Test
    public void sorted_Sorter() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "v",
                1, "z", null,
                0, "a", null,
                1, "x", "m");

        // a case of missing sorter - all rows are considered peers
        IntSeries rn0 = df.over().sorted().denseRank();
        new IntSeriesAsserts(rn0).expectData(1, 1, 1, 1, 1);

        IntSeries rn1 = df.over().sorted($col("a").asc()).denseRank();
        new IntSeriesAsserts(rn1).expectData(2, 3, 2, 1, 2);

        IntSeries rn2 = df.over().sorted($col("b").asc()).denseRank();
        new IntSeriesAsserts(rn2).expectData(2, 3, 4, 1, 2);

        IntSeries rn3 = df.over().sorted($col("c").asc()).denseRank();
        new IntSeriesAsserts(rn3).expectData(1, 2, 3, 3, 1);
    }

    @Test
    public void partitioned_Sorted_Sorter() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        // a case of missing sorter - all rows are considered peers
        IntSeries rn0 = df.over().partitioned("a").sorted().denseRank();
        new IntSeriesAsserts(rn0).expectData(1, 1, 1, 1, 1);

        IntSeries rn1 = df.over().partitioned("a").sorted($col("b").asc()).denseRank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 2, 1, 1);
    }

}
