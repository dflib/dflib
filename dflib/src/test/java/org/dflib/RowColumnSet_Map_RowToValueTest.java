package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_Map_RowToValueTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows()
                .cols("b", "a")
                .map(
                        f -> f.get(1, String.class) + f.get(2),
                        f -> f.getInt(0) * 3);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofInt(0, 2))
                .cols("b", "a")
                .map(
                        f -> f.get(1, String.class) + f.get(2),
                        f -> f.getInt(0) * 3);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(Series.ofBool(true, false, true))
                .cols("b", "a")
                .map(
                        f -> f.get(1, String.class) + f.get(2),
                        f -> f.getInt(0) * 3);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
