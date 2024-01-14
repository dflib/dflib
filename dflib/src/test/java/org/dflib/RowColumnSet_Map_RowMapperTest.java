package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_Map_RowMapperTest {

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
                .map((f, t) -> t.set(0, f.get(1, String.class) + f.get(2)).set(1, f.getInt(0) * 3));

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
                .map((f, t) -> t.set(0, f.get(1, String.class) + f.get(2)).set(1, f.getInt(0) * 3));

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
                .map((f, t) -> t.set(0, f.get(1, String.class) + f.get(2)).set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
