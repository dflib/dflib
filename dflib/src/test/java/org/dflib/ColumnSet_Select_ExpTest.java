package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_Select_ExpTest {

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").select(Exp.$int(0).mul(100), Exp.$int(0).mul(10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).select(Exp.$int(0).mul(100), Exp.$int(0).mul(10));

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().select(Exp.$int(0).mul(100).as("b"), Exp.$int(0).mul(10));

        new DataFrameAsserts(df, "b", "a * 10")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").select(Exp.$int(0).mul(100), Exp.$int(0).mul(10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).select(Exp.$int(0).mul(100), Exp.$int(0).mul(10));

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }
}
