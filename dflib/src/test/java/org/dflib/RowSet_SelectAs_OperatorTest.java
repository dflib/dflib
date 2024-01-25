package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class RowSet_SelectAs_OperatorTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C")
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
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C")
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
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C").expectHeight(0);
    }

    @Test
    public void byIndex_ExpandWithDupes() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2, 0)
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C")
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
                .rowsRange(1, 2)
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C")
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
                .rowsRange(1, 1)
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C").expectHeight(0);
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C")
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
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(2)
                .expectRow(0, -1, "m", "n")
                .expectRow(1, 1, "x", "a");
    }
}
