package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_Merge_RowMapperTest {

    @Test
    public void rowsAll_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows().cols("b", "a")
                .merge((f, t) -> t.set(0, f.get(1, String.class) + f.get(2)).set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByIndex_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(Series.ofInt(0, 2)).cols("b", "a")
                .merge((f, t) -> t.set(0, f.get(1, String.class) + f.get(2)).set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols(0).compactInt(0)
                .rows(Series.ofBool(true, false, true)).cols("b", "a")
                .merge((f, t) -> t.set(0, f.get(1, String.class) + f.get(2)).set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
