package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggMinMaxTest {

    @Test
    public void ints() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 1,
                8, 1);

        DataFrame agg = df.cols().agg(
                $int("a").min(),
                $int("a").max());

        new DataFrameAsserts(agg, "min(a)", "max(a)")
                .expectHeight(1)
                .expectRow(0, -1, 8);
    }


    @Test
    public void longFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, 1L,
                2L, 4L,
                -1L, 5L,
                8L, 2L);

        DataFrame agg = df.cols().agg(
                $long(1).max($long(0).mod(2).eq(0L)),
                $long(1).min($long(0).mod(2).eq(0L)),
                $long("a").max($long("b").mod(2).eq(1L)),
                $long("a").min($long("b").mod(2).eq(1L))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4L, 2L, 1L, -1L);
    }

    @Test
    public void intFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        DataFrame agg = df.cols().agg(
                $int(1).max($int(0).mod(2).eq(0)),
                $int(1).min($int(0).mod(2).eq(0)),
                $int("a").max($int("b").mod(2).eq(1)),
                $int("a").min($int("b").mod(2).eq(1))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4, 2, 1, -1);
    }

    @Test
    public void doubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1., 1.01,
                6.5, 15.7,
                -1.2, 5.1,
                8., 2.);

        DataFrame agg = df.cols().agg(
                $double(1).max($double(0).gt(5)),
                $double(1).min($double(0).gt(5)),
                $double("a").max($double("b").gt(5)),
                $double("a").min($double("b").gt(5))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 15.7, 2.0, 6.5, -1.2);
    }
}
