package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Select_RowMapperTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().select(
                        (f, t) -> {
                            t.set("b", f.get(0, Integer.class) * 100);
                            t.set("c", f.get(0, Integer.class) * 10);
                        }
                );

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void except_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void except_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }
}
