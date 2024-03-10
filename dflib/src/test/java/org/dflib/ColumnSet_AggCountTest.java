package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggCountTest {

    @Test
    public void test() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                0, "a");

        DataFrame agg = df.cols().agg(count());

        new DataFrameAsserts(agg, "count")
                .expectHeight(1)
                .expectRow(0, 2);
    }

    @Test
    public void filtered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols().agg(
                count($int(0).mod(2).eq(0)),
                count($int("b").mod(2).eq(1))
        );

        new DataFrameAsserts(agg, "count", "count_")
                .expectHeight(1)
                .expectRow(0, 1, 3);
    }
}
