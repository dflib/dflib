package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

@Deprecated
public class DataFrame_ExplodeTest {

    @Test
    public void primitiveArray() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        new int[]{1, 2, -1}, "x",
                        new int[]{3}, "y",
                        new int[]{}, "z",
                        null, "a")
                .vExplode("a");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "x")
                .expectRow(2, -1, "x")
                .expectRow(3, 3, "y")
                .expectRow(4, null, "z")
                .expectRow(5, null, "a");
    }

    @Test
    public void array() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        new Integer[]{1, 2, -1}, "x",
                        new Integer[]{3}, "y",
                        new Integer[]{}, "z",
                        null, "a")
                .vExplode("a");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "x")
                .expectRow(2, -1, "x")
                .expectRow(3, 3, "y")
                .expectRow(4, null, "z")
                .expectRow(5, null, "a");
    }

    @Test
    public void list() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        List.of(1, 2, -1), "x",
                        List.of(3), "y",
                        List.of(), "z",
                        null, "a")
                .vExplode("a");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "x")
                .expectRow(2, -1, "x")
                .expectRow(3, 3, "y")
                .expectRow(4, null, "z")
                .expectRow(5, null, "a");
    }

    @Test
    public void scalar() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z",
                        null, "a")
                .vExplode("a");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z")
                .expectRow(3, null, "a");
    }
}
