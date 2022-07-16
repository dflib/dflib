package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Over_MapColumnTest {

    @Test
    public void testEmpty() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        Series<?> r = df.over().mapColumn(Exp.$int("a").sum());
        new SeriesAsserts(r).expectData();
    }

    @Test
    public void testDefault() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().mapColumn(Exp.$int("a").sum());
        new SeriesAsserts(r).expectData(5, 5, 5, 5, 5);
    }

    @Test
    public void testPartitioned() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().partitioned("a").mapColumn(Exp.$int("a").sum());
        new SeriesAsserts(r).expectData(3, 2, 3, 0, 3);
    }

    @Test
    public void testSorted() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().sorted(Exp.$col("b").asc()).mapColumn(Exp.$int("a").sum());
        new SeriesAsserts(r).expectData(5, 5, 5, 5, 5);
    }

    @Test
    public void testPartitioned_Sorted() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().partitioned("a").sorted(Exp.$col("b").asc()).mapColumn(Exp.$int("a").sum());
        new SeriesAsserts(r).expectData(3, 2, 3, 0, 3);
    }

}
