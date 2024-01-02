package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

@Deprecated
public class DataFrame_FillNullsTest {

    @Test
    public void fillNulls() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void fillNulls_Column() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void fillNullsFromSeries() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void fillNullsBackwards() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
    public void fillNullsForward() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
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
