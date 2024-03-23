package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;

public class GroupBy_RankTest {

    @Test
    public void noSort() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.group("a").rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 1, 1, 1);

        IntSeries rn2 = df.group("b").rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void sort() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.group("a").sort("b", true).rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 3, 1, 1);

        IntSeries rn2 = df.group("b").sort("a", true).rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }
    

    @Test
    public void sort_Sorter() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries rn1 = df.group("a").sort($col("b").asc()).rank();
        new IntSeriesAsserts(rn1).expectData(1, 1, 3, 1, 1);

        IntSeries rn2 = df.group("b").sort($col("a").asc()).rank();
        new IntSeriesAsserts(rn2).expectData(1, 1, 1, 1, 1);
    }

}
