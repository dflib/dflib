package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Select_SourceTest {

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "Y", "c").select();

        new DataFrameAsserts(df, "a", "Y", "c")
                .expectHeight(2)
                .expectRow(0, 1, null, "a")
                .expectRow(1, 2, null, "b");
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols(0, 2, 4).select();

        new DataFrameAsserts(df, "a", "c", "4")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }

    @Test
    public void noTarget() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols().select();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }
}
