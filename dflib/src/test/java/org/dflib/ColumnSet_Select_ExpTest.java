package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import static org.dflib.Exp.*;

public class ColumnSet_Select_ExpTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().select($int(0).mul(100).as("b"), $int(0).mul(10));

        new DataFrameAsserts(df, "b", "a * 10")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void append() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c")
                .select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "b_", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void byName_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "b")
                .select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "b", "b_")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 7).select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "b", "7")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void except_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void except_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }
}
