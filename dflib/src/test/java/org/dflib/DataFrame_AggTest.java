package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;

public class DataFrame_AggTest {

    @Test
    public void mix() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df.agg(
                Exp.$long("a").sum(),
                Exp.count(),
                Exp.$double("d").sum());

        new DataFrameAsserts(agg, "sum(a)", "count", "sum(d)")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void count() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                0, "a");

        DataFrame agg = df.agg(Exp.count());

        new DataFrameAsserts(agg, "count")
                .expectHeight(1)
                .expectRow(0, 2);
    }

    @Test
    public void sumInt_SumLong() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5L);

        DataFrame agg = df.agg(
                Exp.$int("a").sum(),
                Exp.$long(1).sum());

        new DataFrameAsserts(agg, "sum(a)", "sum(b)")
                .expectHeight(1)
                .expectRow(0, 0, 6L);
    }

    @Test
    public void minMax() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 1,
                8, 1);

        DataFrame agg = df.agg(
                Exp.$int("a").min(),
                Exp.$int("a").max());

        new DataFrameAsserts(agg, "min(a)", "max(a)")
                .expectHeight(1)
                .expectRow(0, -1, 8);
    }

    @Test
    public void test_vConcat() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                0, "a");

        DataFrame agg = df.agg(
                Exp.$col("a").vConcat("_"),
                Exp.$col(1).vConcat(" ", "[", "]"));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1_0", "[x a]");
    }

    @Test
    public void set() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.agg(Exp.$col("a").set(), Exp.$col(1).set());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, new HashSet<>(asList(1, 2)), new HashSet<>(asList("x", "a")));
    }

    @Test
    public void list() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.agg(Exp.$col("a").list(), Exp.$col(1).list());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, asList(1, 2, 1), asList("x", "x", "a"));
    }

    @Test
    public void average() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                0, 55.5);

        DataFrame agg = df.agg(
                Exp.$int("a").avg(),
                Exp.$double(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void median_odd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.);

        DataFrame agg = df.agg(
                Exp.$int("a").median(),
                Exp.$double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 55.5);
    }

    @Test
    public void median_even() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.,
                3, 5.);

        DataFrame agg = df.agg(
                Exp.$int("a").median(),
                Exp.$double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 2., 30.25);
    }

    @Test
    public void median_zero() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.agg(
                Exp.$int("a").median(),
                Exp.$double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 0., 0.);
    }

    @Test
    public void median_one() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, 100);

        DataFrame agg = df.agg(
                Exp.$int("a").median(),
                Exp.$int(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 100.);
    }

    @Test
    public void median_nulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                0, 55.5,
                4, 0.,
                null, 5.);

        DataFrame agg = df.agg(
                Exp.$int("a").median(),
                Exp.$double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 5.);
    }

    @Test
    public void first() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.agg(
                Exp.$col("a").first(),
                Exp.$col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void first_Empty() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.agg(
                Exp.$col("a").first(),
                Exp.$col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, null, null);
    }

    @Test
    public void first_Nulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                null, 5);

        DataFrame agg = df.agg(
                Exp.$col("a").first(),
                Exp.$col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, null);
    }

    @Test
    public void last() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.agg(
                Exp.$col("a").last(),
                Exp.$col(1).last());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, 5);
    }

    @Test
    public void custom() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.agg(
                Exp.$col(1).agg(Series::size));

        new DataFrameAsserts(agg, "b")
                .expectHeight(1)
                .expectRow(0, 2);
    }
}
