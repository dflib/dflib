package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

public class DataFrame_Over_RankTest {

    @Test
    public void testNoPartition_NoSort_Emtpy() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        IntSeries r = df.over().rank();
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

        IntSeries r = df.over().rank();
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

        IntSeries rna = df.over().partitioned("a").rank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rna).expectData(1, 1, 1, 1, 1);

        IntSeries rnb = df.over().partitioned("b").rank();
        // no sorting - all rows are considered "peers"
        new IntSeriesAsserts(rnb).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void testNoPartition_Sort() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testPartition_Sort() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn = df.over().partitioned("a").sorted("b", true).rank();
        new IntSeriesAsserts(rn).expectData(1, 1, 3, 1, 1);
    }

}
