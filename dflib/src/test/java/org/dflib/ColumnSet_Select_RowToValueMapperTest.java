package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Select_RowToValueMapperTest {

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").select(
                        r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2)
                .select(
                        r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .select(
                        r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10
                );

        new DataFrameAsserts(df, "0", "1")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a")
                .select(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1)
                .select(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }
}
