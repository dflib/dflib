package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;

public class Window_SelectPartitionedTest {

    @Test
    public void cols_Implicit() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over().partitioned("a").select($int("a").sum());
        new DataFrameAsserts(r, "sum(a)")
                .expectHeight(5)
                .expectRow(0, 3)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 0)
                .expectRow(4, 3);
    }
}
