package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_Merge_ExpTest {

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void cols_ByName_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "b").merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsAppend() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c", "b").merge(
                        $int(0).mul(100),
                        $int(0).mul(10),
                        $str(1).mapVal(v -> "[" + v + "]"));

        new DataFrameAsserts(df, "a", "b", "b_", "c", "b__")
                .expectHeight(2)
                .expectRow(0, 1, "x", 100, 10, "[x]")
                .expectRow(1, 2, "y", 200, 20, "[y]");
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().merge($int(0).mul(100).as("b"), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "a * 10")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, "x", 10)
                .expectRow(1, 200, "y", 20);
    }
}
