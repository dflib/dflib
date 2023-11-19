package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Select_ColsTest {

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("X", "Y").select("a", "b");

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void byRepeatingName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("A", "A", "A").select("a", "b", "a");

        new DataFrameAsserts(df, "A", "A_", "A__")
                .expectHeight(2)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 2, "y", 2);
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("X", "Y").select(0, 1);

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }
}
