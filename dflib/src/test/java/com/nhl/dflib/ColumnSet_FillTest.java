package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_FillTest {

    @Test
    public void fill() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fill("B", "C", "*");

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "B", "C", "*")
                .expectRow(1, null, "B", "C", "*")
                .expectRow(2, 3, "B", "C", "*");
    }
}
