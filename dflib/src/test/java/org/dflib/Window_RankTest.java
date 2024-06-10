package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class Window_RankTest {

    @Test
    public void emtpy() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        IntSeries r = df.over().rank();
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

        IntSeries r = df.over().rank();
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

        IntSeries rna = df.over().partitioned("a").rank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rna).expectData(1, 1, 1, 1, 1);

        IntSeries rnb = df.over().partitioned("b").rank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rnb).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.over().sorted("a", true).rank();
        new IntSeriesAsserts(rn1).expectData(2, 5, 2, 1, 2);

        IntSeries rn2 = df.over().sorted("b", true).rank();
        new IntSeriesAsserts(rn2).expectData(2, 4, 5, 1, 2);
    }

    @Test
    public void partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.over().partitioned("a").sorted("b", true).rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 3, 1, 1);

        IntSeries rn2 = df.over().partitioned("b").sorted("a", true).rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }
}
