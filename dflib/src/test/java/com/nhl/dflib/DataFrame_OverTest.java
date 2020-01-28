package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

public class DataFrame_OverTest {

    @Test
    public void testNoPartition_NoSort_RowNumbers_Emtpy() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        IntSeries rn = df.over().rowNumbers(2);
        new IntSeriesAsserts(rn).expectData();
    }

    @Test
    public void testNoPartition_NoSort_RowNumbers() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().rowNumbers();
        new IntSeriesAsserts(rn).expectData(0, 1, 2, 3, 4);
    }

    @Test
    public void testNoPartition_NoSort_RowNumbersOffset() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().rowNumbers(2);
        new IntSeriesAsserts(rn).expectData(2, 3, 4, 5, 6);
    }

    @Test
    public void testPartition_NoSort_RowNumbers() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rna = df.over().partitioned("a").rowNumbers();
        new IntSeriesAsserts(rna).expectData(0, 0, 1, 0, 2);

        IntSeries rnb = df.over().partitioned("b").rowNumbers();
        new IntSeriesAsserts(rnb).expectData(0, 0, 0, 0, 1);
    }

    @Test
    public void testNoPartition_Sort_RowNumbers() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().sorted("c", true).rowNumbers();
        new IntSeriesAsserts(rn).expectData(3, 2, 0, 1, 4);
    }

    @Test
    public void testPartition_Sort_RowNumbers() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().partitioned("a").sorted("c", true).rowNumbers();
        new IntSeriesAsserts(rn).expectData(1, 0, 0, 0, 2);
    }

}
