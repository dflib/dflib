package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Over_ShiftTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        Series<Object> r = df.over().shift("b", -1);
        new SeriesAsserts(r).expectData();
    }

    @Test
    public void default_One() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, "x");

        Series<Integer> s1 = df.over().shift("b", 1);
        new SeriesAsserts(s1).expectData(new Object[]{null});

        Series<Integer> s2 = df.over().shift(0, -1);
        new SeriesAsserts(s2).expectData(new Object[]{null});

        Series<Integer> s3 = df.over().shift("b", 2);
        new SeriesAsserts(s3).expectData(new Object[]{null});

        Series<Integer> s4 = df.over().shift("b", 0);
        new SeriesAsserts(s4).expectData("x");
    }

    @Test
    public void default_Many() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<Integer> s1 = df.over().shift("a", 2);
        new SeriesAsserts(s1).expectData(null, null, 1, 2, 1);

        Series<Integer> s2 = df.over().shift(0, 2);
        new SeriesAsserts(s2).expectData(null, null, 1, 2, 1);

        Series<Integer> s3 = df.over().shift(0, 6);
        new SeriesAsserts(s3).expectData(null, null, null, null, null);
    }

    @Test
    public void partitioned() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                2, "y",
                1, "x");

        Series<String> sa = df.over().partitioned("a").shift("b", 1);
        new SeriesAsserts(sa).expectData(null, null, "x", null, "y", "z");

        Series<String> sb = df.over().partitioned("b").shift("b", -1);
        new SeriesAsserts(sb).expectData("x", "y", null, null, null, null);
    }

    @Test
    public void sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<String> sa = df.over().sorted("a", true).shift("b", 1);
        new SeriesAsserts(sa).expectData("a", "x", "x", null, "z");

        Series<String> sb = df.over().sorted("b", true).shift("b", 1);
        new SeriesAsserts(sb).expectData("a", "x", "y", null, "x");
    }

    @Test
    public void partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<String> sa = df.over().partitioned("a").sorted("b", true).shift("b", 1);
        new SeriesAsserts(sa).expectData(null, "x", "x", null, null);

        Series<String> sb = df.over().partitioned("b").sorted("a", true).shift("b", 1);
        new SeriesAsserts(sb).expectData("x", null, null, null, null);
    }
}
