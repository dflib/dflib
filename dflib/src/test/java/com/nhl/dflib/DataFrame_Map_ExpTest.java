package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Map_ExpTest {

    @Test
    public void map_Exp() {
        DataFrame df = DataFrame
                .foldByRow("a", "b").of(1, "x", 2, "y")
                .map(Exp.$int(0).mul(10).as("a"), Exp.$col(1).as("b"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }
}
