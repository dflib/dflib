package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Over_DenseRankTest {

    @Test
    public void testNoPartition_NoSort_Emtpy() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        IntSeries r = df.over().denseRank();
        new IntSeriesAsserts(r).expectData();
    }

    @Test
    public void testNoPartition_NoSort() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testPartition_NoSort() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testNoPartition_Sort() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
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
    public void testPartition_Sort() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn = df.over().partitioned("a").sorted("b", true).denseRank();
        new IntSeriesAsserts(rn).expectData(1, 1, 2, 1, 1);
    }

}
