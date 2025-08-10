package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_StackTest {

    @Test
    public void stack() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        null, "z",
                        2, "y")
                .stack().select();

        new DataFrameAsserts(df, "row", "column", "value")
                .expectHeight(5)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 2, "a", 2)
                .expectRow(2, 0, "b", "x")
                .expectRow(3, 1, "b", "z")
                .expectRow(4, 2, "b", "y");
    }

    @Test
    public void stack_rows() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", "A",
                        null, "z", "A",
                        2, "y", "B")
                .stack().rows("c").select();

        new DataFrameAsserts(df, "c", "column", "value")
                .expectHeight(5)
                .expectRow(0, "A", "a", 1)
                .expectRow(1, "B", "a", 2)
                .expectRow(2, "A", "b", "x")
                .expectRow(3, "A", "b", "z")
                .expectRow(4, "B", "b", "y");
    }

    @Test
    public void stack_includeNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                null, null,
                2, "y").stack().includeNulls().select();

        new DataFrameAsserts(df, "row", "column", "value")
                .expectHeight(6)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 1, "a", null)
                .expectRow(2, 2, "a", 2)
                .expectRow(3, 0, "b", "x")
                .expectRow(4, 1, "b", null)
                .expectRow(5, 2, "b", "y");
    }

    @Test
    public void stack_rows_includeNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", "A",
                        null, "z", "A",
                        2, "y", "B")
                .stack().rows("c").includeNulls().select();

        new DataFrameAsserts(df, "c", "column", "value")
                .expectHeight(6)
                .expectRow(0, "A", "a", 1)
                .expectRow(1, "A", "a", null)
                .expectRow(2, "B", "a", 2)
                .expectRow(3, "A", "b", "x")
                .expectRow(4, "A", "b", "z")
                .expectRow(5, "B", "b", "y");
    }

    @Deprecated
    @Test
    public void stackIncludeNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                null, null,
                2, "y").stackIncludeNulls();

        new DataFrameAsserts(df, "row", "column", "value")
                .expectHeight(6)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 1, "a", null)
                .expectRow(2, 2, "a", 2)
                .expectRow(3, 0, "b", "x")
                .expectRow(4, 1, "b", null)
                .expectRow(5, 2, "b", "y");
    }
}
