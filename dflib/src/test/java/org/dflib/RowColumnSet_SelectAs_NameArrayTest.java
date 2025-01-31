package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_SelectAs_NameArrayTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .cols("b", "a")
                .selectAs("X", "Y");

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(3)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2)
                .expectRow(2, "m", -1);
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .cols("b", "a")
                .selectAs("X", "Y");

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "m", -1);
    }

    @Test
    public void byIndex_AddRows_AddCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2)
                .cols("b", "a", "x")
                .selectAs("X", "Y", "Z");

        new DataFrameAsserts(df, "X", "Y", "Z")
                .expectHeight(3)
                .expectRow(0, "x", 1, null)
                .expectRow(1, "m", -1, null)
                .expectRow(2, "m", -1, null);
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .cols("b", "a")
                .selectAs("X", "Y");

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "m", -1);
    }
}
