package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Map_RowMapperTest {

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").map((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).map((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10)
                );

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void cols_ByMapper() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().map(
                        (f, t) -> {
                            t.set("b", f.get(0, Integer.class) * 100);
                            t.set("c", f.get(0, Integer.class) * 10);
                        }
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").map((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).map((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, "x", 10)
                .expectRow(1, 200, "y", 20);
    }
}
