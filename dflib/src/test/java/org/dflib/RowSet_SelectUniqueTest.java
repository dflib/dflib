package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;

@Deprecated
public class RowSet_SelectUniqueTest {

    @Test
    public void emptyAll() {
        DataFrame df = DataFrame.empty("a", "b", "c").rows().selectUnique("a");
        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void empty_ByCondition() {
        DataFrame df = DataFrame.empty("a", "b", "c").rows($col("a").isNotNull()).selectUnique("a");
        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g",
                        1, "m", "n")
                .rows().selectUnique("a");

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
                .rows().selectUnique("a", "b");

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
                        1, "x", "a", // <--
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g", // <--
                        1, "m", "n") // <--
                .rows(0, 3, 4).selectUnique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(1)
                .expectRow(0, 1, "x", "a");
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
                .rows(0, 2, 3, 4).selectUnique("a", "b");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 1, "e", "k")
                .expectRow(2, 1, "m", "n");
    }

    @Test
    public void byIndex_IntCol() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g", // <--
                        1, "m", "n") // <--
                .rows(0, 3, 4).selectUnique(0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(1)
                .expectRow(0, 1, "x", "a");
    }

    @Test
    public void byIndex_AllUnique() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        2, "y", "b",
                        3, "e", "k",
                        4, "f", "g", // <--
                        5, "m", "n") // <--
                .rows(0, 3, 4).selectUnique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 4, "f", "g")
                .expectRow(2, 5, "m", "n");
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
                .rows(Series.ofInt(1, 2, 4)).selectUnique();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 2, "y", "b")
                .expectRow(1, 1, "x", "a");
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
                .rowsRange(0, 4).selectUnique(0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a", // <--
                        2, "y", "b",
                        1, "e", "k",
                        1, "f", "g", // <--
                        3, "o", "p", // <--
                        1, "m", "n") // <--
                .rows(Series.ofBool(true, false, false, true, true, true)).selectUnique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 3, "o", "p");
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
                .rows(Series.ofBool(true, false, false, true, true, true)).selectUnique(0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 3, "o", "p");
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
                .rows(Series.ofBool(true, false, false, true, true)).selectUnique("a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 4, "f", "g")
                .expectRow(2, 5, "m", "n");
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
                .rows(Series.ofBool(false, true, true, false, true)).selectUnique();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 2, "y", "b")
                .expectRow(1, 1, "x", "a");
    }
}
