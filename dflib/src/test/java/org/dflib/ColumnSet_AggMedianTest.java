package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggMedianTest {

    @Test
    public void odd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 55.5);
    }

    @Test
    public void even() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.,
                3, 5.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 2., 30.25);
    }

    @Test
    public void zero() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 0., 0.);
    }

    @Test
    public void one() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, 100);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $int(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 100.);
    }

    @Test
    public void nulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                0, 55.5,
                4, 0.,
                null, 5.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 5.);
    }
}
