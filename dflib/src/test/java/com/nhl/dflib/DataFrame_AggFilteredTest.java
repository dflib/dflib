package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFrame_AggFilteredTest {

    @Test
    public void sumInt() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Exp.$int(1).sum(Exp.$int(0).mod(2).eq(0)),
                // filter is applied to column 1, sum is applied to column 0
                Exp.$int("a").sum(Exp.$int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)")
                .expectHeight(1)
                .expectRow(0, 11, -4);
    }

    @Test
    public void sumLong() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Exp.$long(1).sum(Exp.$int(0).mod(2).eq(0)),
                // filter is applied to column 1, sum is applied to column 0
                Exp.$long("a").sum(Exp.$int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)")
                .expectHeight(1)
                .expectRow(0, 11L, -4L);
    }

    @Test
    public void sumDouble() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.1, 1.8,
                -1., 5.8,
                2.35, 6.5,
                4.6, 5.1);


        DataFrame agg = df.agg(
                // filter is applied to column 0, sum is applied to column 1
                Exp.$double(1).sum(Exp.$double(0).lt(4)),
                // filter is applied to column 1, sum is applied to column 0
                Exp.$double("a").sum(Exp.$double("b").gt(5)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)").expectHeight(1);

        assertEquals(14.1, (Double) agg.getColumn("sum(b)").get(0), 0.000000001);
        assertEquals(5.95, (Double) agg.getColumn("sum(a)").get(0), 0.000000001);
    }

    @Test
    public void first_Condition() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.agg(
                Exp.$col(1).first(Exp.$int(0).mod(2).eq(0)),
                Exp.$col("a").first(Exp.$int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, 5, 7);
    }

    @Test
    public void first_Condition_Never() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.agg(
                Exp.$col(1).first(Exp.$val(false).castAsBool()),
                Exp.$col("a").first(Exp.$int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, null, 7);
    }

    @Test
    public void count() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.agg(
                Exp.count(Exp.$int(0).mod(2).eq(0)),
                Exp.count(Exp.$int("b").mod(2).eq(1))
        );

        new DataFrameAsserts(agg, "count", "count_")
                .expectHeight(1)
                .expectRow(0, 1, 3);
    }

    @Test
    public void vConcat() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5,
                8, 8);

        DataFrame agg = df.agg(
                Exp.$col(1).vConcat(Exp.$int(0).mod(2).eq(0), "_"),
                Exp.$col("a").vConcat(Exp.$int("b").mod(2).eq(1), ", ", "[", "]"));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, "5_8", "[7, -1, -4]");
    }

    @Test
    public void minMaxLong() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, 1L,
                2L, 4L,
                -1L, 5L,
                8L, 2L);

        DataFrame agg = df.agg(
                Exp.$long(1).max(Exp.$long(0).mod(2).eq(0L)),
                Exp.$long(1).min(Exp.$long(0).mod(2).eq(0L)),
                Exp.$long("a").max(Exp.$long("b").mod(2).eq(1L)),
                Exp.$long("a").min(Exp.$long("b").mod(2).eq(1L))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4L, 2L, 1L, -1L);
    }

    @Test
    public void minMaxInt() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        DataFrame agg = df.agg(
                Exp.$int(1).max(Exp.$int(0).mod(2).eq(0)),
                Exp.$int(1).min(Exp.$int(0).mod(2).eq(0)),
                Exp.$int("a").max(Exp.$int("b").mod(2).eq(1)),
                Exp.$int("a").min(Exp.$int("b").mod(2).eq(1))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4, 2, 1, -1);
    }

    @Test
    public void minMaxDouble() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1., 1.01,
                6.5, 15.7,
                -1.2, 5.1,
                8., 2.);

        DataFrame agg = df.agg(
                Exp.$double(1).max(Exp.$double(0).gt(5)),
                Exp.$double(1).min(Exp.$double(0).gt(5)),
                Exp.$double("a").max(Exp.$double("b").gt(5)),
                Exp.$double("a").min(Exp.$double("b").gt(5))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 15.7, 2.0, 6.5, -1.2);
    }

    @Test
    public void averageDouble() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                5, 8L,
                0, 55.5);

        DataFrame agg = df.agg(
                Exp.$double("a").avg(Exp.$int(0).ne(5)),
                Exp.$double(1).avg(Exp.$int(0).ne(5)));

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

}
