package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.dflib.Exp.*;

public class ColumnSet_AggSumTest {

    @Test
    public void int_long() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5L);

        DataFrame agg = df.cols().agg(
                $int("a").sum(),
                $long(1).sum());

        new DataFrameAsserts(agg, "sum(a)", "sum(b)")
                .expectHeight(1)
                .expectRow(0, 0L, 6L);
    }

    @Test
    public void intFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.cols().agg(
                // filter is applied to column 0, sum is applied to column 1
                $int(1).sum($int(0).mod(2).eq(0)),
                // filter is applied to column 1, sum is applied to column 0
                $int("a").sum($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)")
                .expectHeight(1)
                .expectRow(0, 11L, -4L);
    }

    @Test
    public void longFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.cols().agg(
                // filter is applied to column 0, sum is applied to column 1
                $long(1).sum($int(0).mod(2).eq(0)),
                // filter is applied to column 1, sum is applied to column 0
                $long("a").sum($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)")
                .expectHeight(1)
                .expectRow(0, 11L, -4L);
    }

    @Test
    public void doubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.1, 1.8,
                -1., 5.8,
                2.35, 6.5,
                4.6, 5.1);


        DataFrame agg = df.cols().agg(
                // filter is applied to column 0, sum is applied to column 1
                $double(1).sum($double(0).lt(4)),
                // filter is applied to column 1, sum is applied to column 0
                $double("a").sum($double("b").gt(5)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)").expectHeight(1);

        assertEquals(14.1, (Double) agg.getColumn("sum(b)").get(0), 0.000000001);
        assertEquals(5.95, (Double) agg.getColumn("sum(a)").get(0), 0.000000001);
    }

    @Test
    public void sum_div_mul() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1L,
                -3, 5L);

        DataFrame agg = df.cols().agg(
                $int("a").sum().div(2),
                $long(1).sum().mul(2));

        new DataFrameAsserts(agg, "sum(a) / castAsLong(2)", "sum(b) * castAsLong(2)")
                .expectHeight(1)
                .expectRow(0, -1L, 12L);
    }

    @Test
    public void sum_div_median() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 1L,
                6, 7L,
                -3, 5L);

        DataFrame agg = df.cols("A", "B").agg(
                $int("a").sum().div($int("a").median()),
                $long("b").sum().div($long("b").median()));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 2.5, 2.6);
    }

}
