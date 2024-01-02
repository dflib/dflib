package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_FillNullsTest {

    @Test
    public void fillNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNulls("*");

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "*", "*")
                .expectRow(1, null, "*", "a", "*")
                .expectRow(2, 3, "*", "*", "*");
    }

    @Test
    public void fillNullsBackwards() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNullsBackwards();

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, null, null, "a", null)
                .expectRow(2, 3, null, null, null);
    }

    @Test
    public void fillNullsForward() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNullsForward();

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, null, "x", "a", null)
                .expectRow(2, 3, "x", "a", null);
    }

    @Test
    public void fillNullsFromSeries() {

        Series<String> filler = Series.of("row1", "row2", "row3");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNullsFromSeries(filler);

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "row1", "row1")
                .expectRow(1, null, "row2", "a", "row2")
                .expectRow(2, 3, "row3", "row3", "row3");
    }
}
