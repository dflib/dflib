package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class RowSet_SelectRename_NameArrayTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -1, "m", "n");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, -1, "m", "n");
    }

    @Test
    public void byIndex_Empty() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt())
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z").expectHeight(0);
    }

    @Test
    public void byIndex_ExpandWithDupes() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2, 0)
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, -1, "m", "n")
                .expectRow(2, -1, "m", "n")
                .expectRow(3, 1, "x", "a");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRangeOpenClosed(1, 2)
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z")
                .expectHeight(1)
                .expectRow(0, 2, "y", "b");
    }

    @Test
    public void byRange_Empty() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRangeOpenClosed(1, 1)
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z").expectHeight(0);
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, -1, "m", "n");
    }

    @Test
    public void sample() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsSample(2, new Random(9))
                .selectRename("x", "y", "z");

        new DataFrameAsserts(df, "x", "y", "z")
                .expectHeight(2)
                .expectRow(0, -1, "m", "n")
                .expectRow(1, 1, "x", "a");
    }
}
