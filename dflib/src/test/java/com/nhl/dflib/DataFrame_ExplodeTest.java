package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DataFrame_ExplodeTest {

    @Test
    public void testPrimitiveArray() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                        new int[]{1, 2, -1}, "x",
                        new int[]{3}, "y",
                        new int[]{}, "z",
                        null, "a")
                .explode("a");

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
    public void testArray() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                        new Integer[]{1, 2, -1}, "x",
                        new Integer[]{3}, "y",
                        new Integer[]{}, "z",
                        null, "a")
                .explode("a");

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
    public void testList() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                        List.of(1, 2, -1), "x",
                        List.of(3), "y",
                        List.of(), "z",
                        null, "a")
                .explode("a");

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
    public void testScalar() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                        1, "x",
                        2, "y",
                        3, "z",
                        null, "a")
                .explode("a");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z")
                .expectRow(3, null, "a");
    }
}
