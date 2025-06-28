package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggTest {

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols()
                .agg(
                        $long("a").sum(),
                        count(),
                        $double("d").sum());

        new DataFrameAsserts(agg, "sum(a)", "count", "sum(d)")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols("sum_a", "count", "sum_d")
                .agg(
                        $long("a").sum(),
                        count(),
                        $double("d").sum());

        new DataFrameAsserts(agg, "sum_a", "count", "sum_d")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void byName_StrExp() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols("sum_a", "count", "sum_d")
                .agg(
                        "sum(long(a))",
                        "count()",
                        "sum(double(d))");

        new DataFrameAsserts(agg, "sum_a", "count", "sum_d")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols(0, 2, 3)
                .agg(
                        $long("a").sum(),
                        count(),
                        $double("d").sum());

        new DataFrameAsserts(agg, "a", "c", "d")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }
}
