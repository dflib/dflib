package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_UniqueTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g",
                        1, "m", "n")
                .rows()
                .unique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void all_TwoColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "x", "b",
                        1, "x", "k",
                        1, "f", "g",
                        1, "m", "n")
                .rows()
                .unique("a", "b");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "x", "b")
                .expectRow(2, 1, "f", "g")
                .expectRow(3, 1, "m", "n");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g",
                        1, "m", "n")
                .rows(Series.ofInt(0, 3, 4))
                .unique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "e", "k");
    }

    @Test
    public void byIndex_TwoColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        1, "x", "b",
                        1, "e", "k", // <--
                        1, "x", "g", // <--
                        1, "m", "n") // <--
                .rows(Series.ofInt(0, 2, 3, 4))
                .unique("a", "b");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 1, "x", "b")
                .expectRow(2, 1, "e", "k")
                .expectRow(3, 1, "m", "n");
    }

    @Test
    public void byIndex_IntCol() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g",
                        1, "m", "n")
                .rows(Series.ofInt(0, 3, 4))
                .unique(0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "e", "k");
    }

    @Test
    public void byIndex_AllUnique() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        3, "e", "k",
                        4, "f", "g",
                        5, "m", "n")
                .rows(Series.ofInt(0, 3, 4))
                .unique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 3, "e", "k")
                .expectRow(3, 4, "f", "g")
                .expectRow(4, 5, "m", "n");
    }

    @Test
    public void byIndex_AllColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b", // <--
                        1, "x", "a", // <--
                        3, "f", "g",
                        1, "x", "a") // <--
                .rows(Series.ofInt(1, 2, 4))
                .unique();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "x", "a")
                .expectRow(3, 3, "f", "g");
    }

    @Test
    public void byRange_IntCol() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <-
                        2, "y", "b", // <-
                        1, "e", "k", // <-
                        1, "f", "g", // <-
                        1, "m", "n")
                .rowsRangeOpenClosed(0, 4)
                .unique(0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "m", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g",
                        3, "o", "p",
                        1, "m", "n")
                .rows(Series.ofBool(true, false, false, true, true, true))
                .unique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "e", "k")
                .expectRow(3, 3, "o", "p");
    }

    @Test
    public void byCondition_IntCol() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g",
                        3, "o", "p",
                        1, "m", "n")
                .rows(Series.ofBool(true, false, false, true, true, true))
                .unique(0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "e", "k")
                .expectRow(3, 3, "o", "p");
    }


    @Test
    public void byCondition_AllUnique() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        3, "e", "k",
                        4, "f", "g",
                        5, "m", "n")
                .rows(Series.ofBool(true, false, false, true, true))
                .unique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(5)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 3, "e", "k")
                .expectRow(3, 4, "f", "g")
                .expectRow(4, 5, "m", "n");
    }

    @Test
    public void byCondition_AllColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b", // <--
                        1, "x", "a", // <--
                        3, "f", "g",
                        1, "x", "a") // <--
                .rows(Series.ofBool(false, true, true, false, true))
                .unique();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, 1, "x", "a")
                .expectRow(3, 3, "f", "g");
    }
}
