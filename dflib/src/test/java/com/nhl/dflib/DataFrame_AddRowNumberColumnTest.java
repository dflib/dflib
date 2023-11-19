package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

@Deprecated
public class DataFrame_AddRowNumberColumnTest {

    @Test
    public void addRowNumberColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .addRowNumberColumn("rn");

        new DataFrameAsserts(df, "a", "b", "rn")
                .expectHeight(2)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 2, "y", 2);
    }
}
