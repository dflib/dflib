package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Select_RowsTailTest {

    @Test
    public void tail() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .rowsTail(2).select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 2, "y")
                .expectRow(1, 3, "z");
    }

    @Test
    public void tail_Zero() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .rowsTail(0).select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void tail_OutOfBounds() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .rowsTail(4).select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");
    }

    @Test
    public void negative() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .rowsTail(-2).select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, "x");
    }
}
