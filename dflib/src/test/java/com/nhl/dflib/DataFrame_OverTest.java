package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

public class DataFrame_OverTest {

    @Test
    public void testNoPartition_NoSort_RowNumber_Emtpy() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        IntSeries rn = df.over().rowNumber();
        new IntSeriesAsserts(rn).expectData();
    }

    @Test
    public void testNoPartition_NoSort_RowNumber() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().rowNumber();
        new IntSeriesAsserts(rn).expectData(1, 2, 3, 4, 5);
    }

    @Test
    public void testPartition_NoSort_RowNumber() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rna = df.over().partitioned("a").rowNumber();
        new IntSeriesAsserts(rna).expectData(1, 1, 2, 1, 3);

        IntSeries rnb = df.over().partitioned("b").rowNumber();
        new IntSeriesAsserts(rnb).expectData(1, 1, 1, 1, 2);
    }

    @Test
    public void testNoPartition_Sort_RowNumber() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().sorted("c", true).rowNumber();
        new IntSeriesAsserts(rn).expectData(4, 3, 1, 2, 5);
    }

    @Test
    public void testPartition_Sort_RowNumber() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().partitioned("a").sorted("c", true).rowNumber();
        new IntSeriesAsserts(rn).expectData(2, 1, 1, 1, 3);
    }

}
