package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_DropTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows().cols("b", "a")
                .drop();

        new DataFrameAsserts(df, "c").expectHeight(0);
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2).cols("b", "a")
                .drop();

        new DataFrameAsserts(df, "c")
                .expectHeight(1)
                .expectRow(0, "b");
    }

    @Test
    public void byIndex_ExpandRows_ExpandCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2).cols("b", "a", "x")
                .drop();

        new DataFrameAsserts(df, "c")
                .expectHeight(1)
                .expectRow(0, "b");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true)).cols("b", "a")
                .drop();

        new DataFrameAsserts(df, "c")
                .expectHeight(1)
                .expectRow(0, "b");
    }
}
