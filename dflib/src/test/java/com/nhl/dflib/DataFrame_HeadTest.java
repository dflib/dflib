package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_HeadTest {

    @Test
    public void testHead() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y",
                3, "z")
                .head(2);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testHead_Zero() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y",
                3, "z")
                .head(0);

        new DFAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void testHead_OutOfBounds() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y",
                3, "z")
                .head(4);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");
    }
}
