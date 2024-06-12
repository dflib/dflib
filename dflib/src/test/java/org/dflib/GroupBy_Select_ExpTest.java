package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class GroupBy_Select_ExpTest {

    @Test
    public void cols_Implicit() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "z");

        DataFrame df = df1.group("a").select(rowNum(), $col("b"));

        // must be sorted by groups in the order they are encountered
        new DataFrameAsserts(df, "rowNum()", "b")
                .expectHeight(5)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z")
                .expectRow(3, 1, "y")
                .expectRow(4, 1, "a");
    }

    @Test
    public void head() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df2 = df1.group("a")
                .head(2)
                .select(rowNum());

        new DataFrameAsserts(df2, "rowNum()")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 1)
                .expectRow(3, 1);
    }

    @Test
    public void cols_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols("X").select(rowNum());

        new DataFrameAsserts(df, "X")
                .expectHeight(5)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 1)
                .expectRow(4, 1);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols(1, 2).select(rowNum(), $val("c"));

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(5)
                .expectRow(0, 1, "c")
                .expectRow(1, 2, "c")
                .expectRow(2, 3, "c")
                .expectRow(3, 1, "c")
                .expectRow(4, 1, "c");
    }

    @Test
    public void cols_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols(c -> c.startsWith("b")).select(rowNum());

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 1)
                .expectRow(4, 1);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept("a").select(rowNum());

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 1)
                .expectRow(4, 1);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept(0).select(rowNum());

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 1)
                .expectRow(4, 1);
    }

    @Test
    public void colsExcept_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept(c -> c.startsWith("a")).select(rowNum());

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 1)
                .expectRow(4, 1);
    }
}
