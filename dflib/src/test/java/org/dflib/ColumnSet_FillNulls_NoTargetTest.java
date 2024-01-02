package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_FillNulls_NoTargetTest {

    @Test
    public void fillNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNulls("*");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "*")
                .expectRow(1, "*", "*", "a")
                .expectRow(2, 3, "*", "*");
    }

    @Test
    public void fillNullsBackwards() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNullsBackwards();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 3, null, "a")
                .expectRow(2, 3, null, null);
    }

    @Test
    public void fillNullsForward() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNullsForward();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", null)
                .expectRow(1, 1, "x", "a")
                .expectRow(2, 3, "x", "a");
    }

    @Test
    public void fillNullsFromSeries() {

        Series<String> filler = Series.of("row1", "row2", "row3");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNullsFromSeries(filler);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "row1")
                .expectRow(1, "row2", "row2", "a")
                .expectRow(2, 3, "row3", "row3");
    }
}
