package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Over_RowNumberTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        IntSeries rn = df.over().rowNumber();
        new IntSeriesAsserts(rn).expectData();
    }

    @Test
    public void test() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn = df.over().rowNumber();
        new IntSeriesAsserts(rn).expectData(1, 2, 3, 4, 5);
    }

    @Test
    public void partitioned() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
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
    public void sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "3",
                2, "y", "4",
                1, "z", "2",
                0, "a", "1",
                1, "x", "5");

        IntSeries rn1 = df.over().sorted("c", true).rowNumber();
        new IntSeriesAsserts(rn1).expectData(3, 4, 2, 1, 5);

        IntSeries rn2 = df.over().sorted("b", true).rowNumber();
        new IntSeriesAsserts(rn2).expectData(2, 4, 5, 1, 3);

        IntSeries rn3 = df.over().sorted("a", true).rowNumber();
        new IntSeriesAsserts(rn3).expectData(2, 5, 3, 1, 4);
    }

    @Test
    public void partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s");

        IntSeries rn1 = df.over().partitioned("a").sorted("c", true).rowNumber();
        new IntSeriesAsserts(rn1).expectData(2, 1, 1, 1, 3);

        IntSeries rn2 = df.over().partitioned("a").sorted("b", true).rowNumber();
        new IntSeriesAsserts(rn2).expectData(1, 1, 3, 1, 2);
    }
}
