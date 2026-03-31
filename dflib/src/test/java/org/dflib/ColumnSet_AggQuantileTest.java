package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$date;

public class ColumnSet_AggQuantileTest {

    @Test
    public void filtered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, true,
                100, false,
                2, true,
                200, false,
                3, true);

        DataFrame agg = df.cols().agg(
                $int("a").quantile(0.25, $bool("b")),
                $int("a").quantile(0.75, $bool("b")));

        new DataFrameAsserts(agg, "quantile(a)", "quantile(a)_")
                .expectHeight(1)
                .expectRow(0, 1.5, 2.5);
    }

    @Test
    public void date_odd() {
        DataFrame df = DataFrame.foldByRow("a").of(
                LocalDate.of(2000, 2, 1),
                LocalDate.of(2000, 2, 2),
                LocalDate.of(2000, 2, 3),
                LocalDate.of(2000, 2, 4),
                LocalDate.of(2000, 2, 5));

        DataFrame agg = df.cols().agg(
                $date("a").quantile(0.25),
                $date("a").quantile(0.75));

        new DataFrameAsserts(agg, "quantile(a)", "quantile(a)_")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 2, 2), LocalDate.of(2000, 2, 4));
    }

    @Test
    public void date_even() {
        DataFrame df = DataFrame.foldByRow("a").of(
                LocalDate.of(2000, 2, 1),
                LocalDate.of(2000, 2, 2),
                LocalDate.of(2000, 2, 5),
                LocalDate.of(2000, 2, 8));

        DataFrame agg = df.cols().agg(
                $date("a").quantile(0.25),
                $date("a").quantile(0.75));

        new DataFrameAsserts(agg, "quantile(a)", "quantile(a)_")
                .expectHeight(1)
                .expectRow(0, LocalDate.of(2000, 2, 2), LocalDate.of(2000, 2, 6));
    }
}
