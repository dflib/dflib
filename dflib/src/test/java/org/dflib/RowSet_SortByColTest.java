package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_SortByColTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        4, "e", "k",
                        0, "f", "g")
                .rows()
                .sort("a", true);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 1, "x", "a")
                .expectRow(2, 2, "y", "b")
                .expectRow(3, 4, "e", "k");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        2, "y", "b",
                        4, "e", "k", // <--
                        0, "f", "g") // <--
                .rows(Series.ofInt(0, 2, 3))
                .sort("a", true);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 0, "f", "g")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "x", "a")
                .expectRow(3, 4, "e", "k");
    }
}
