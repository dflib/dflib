package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Map_DataFrameTest {

    @Test
    public void map_UnaryOp() {
        DataFrame df = DataFrame
                .foldByRow("a", "b").of(1, "x", 2, "y")
                .map(f -> f.cols("b").drop());

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }
}
