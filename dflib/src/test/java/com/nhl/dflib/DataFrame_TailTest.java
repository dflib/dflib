package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_TailTest {

    @Test
    public void testTail() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                3, "z")
                .tail(2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 2, "y")
                .expectRow(1, 3, "z");
    }

    @Test
    public void testTail_Zero() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                3, "z")
                .tail(0);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void testTail_OutOfBounds() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                3, "z")
                .tail(4);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");
    }
}
