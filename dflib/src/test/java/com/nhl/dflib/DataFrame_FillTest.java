package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_FillTest {

    @Test
    public void testFillNulls() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "a", 1,
                null, 5,
                "b", null,
                null, null).fillNulls("A");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, "A", 5)
                .expectRow(2, "b", "A")
                .expectRow(3, "A", "A");
    }

    @Test
    public void testFillNulls_Column() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "a", 1,
                null, 5,
                "b", null,
                null, null).fillNulls("b", "A");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, null, 5)
                .expectRow(2, "b", "A")
                .expectRow(3, null, "A");
    }

    @Test
    public void testFillNullsFromSeries() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "a", 1,
                null, 5,
                "b", null,
                null, null).fillNullsFromSeries("b", Series.of("Q", "R", "S", "T"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, null, 5)
                .expectRow(2, "b", "S")
                .expectRow(3, null, "T");
    }

    @Test
    public void testFillNullsBackwards() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "a", null,
                null, 5,
                "b", null,
                "c", 8,
                null, null).fillNullsBackwards("b");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, "a", 5)
                .expectRow(1, null, 5)
                .expectRow(2, "b", 8)
                .expectRow(3, "c", 8)
                .expectRow(4, null, null);
    }

    @Test
    public void testFillNullsForward() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "a", null,
                null, 5,
                "b", null,
                "c", 8,
                null, null).fillNullsForward("b");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, "a", null)
                .expectRow(1, null, 5)
                .expectRow(2, "b", 5)
                .expectRow(3, "c", 8)
                .expectRow(4, null, 8);
    }
}
