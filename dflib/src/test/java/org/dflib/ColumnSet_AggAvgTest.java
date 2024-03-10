package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggAvgTest {

    @Test
    public void intDouble() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                0, 55.5);

        DataFrame agg = df.cols().agg(
                $int("a").avg(),
                $double(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void doubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                5, 8L,
                0, 55.5);

        DataFrame agg = df.cols().agg(
                $double("a").avg($int(0).ne(5)),
                $double(1).avg($int(0).ne(5)));

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }
}
