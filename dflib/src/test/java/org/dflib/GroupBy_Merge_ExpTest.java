package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$val;
import static org.dflib.Exp.rowNum;

public class GroupBy_Merge_ExpTest {

    @Test
    public void cols_Implicit() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "z");

        DataFrame df = df1.group("a").merge(rowNum());
        new DataFrameAsserts(df, "a", "b", "rowNum()")
                .expectHeight(5)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 1, "y", 2)
                .expectRow(2, 1, "z", 3)
                .expectRow(3, 2, "y", 1)
                .expectRow(4, 0, "a", 1);
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
                .merge(rowNum());

        new DataFrameAsserts(df2, "a", "b", "rowNum()")
                .expectHeight(4)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 1, "y", 2)
                .expectRow(2, 2, "y", 1)
                .expectRow(3, 0, "a", 1);
    }

    @Test
    public void cols_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols("X").merge(rowNum());

        new DataFrameAsserts(df, "a", "b", "X")
                .expectHeight(5)
                .expectRow(0, 1, "a", 1)
                .expectRow(1, 1, "c", 2)
                .expectRow(2, 1, "e", 3)
                .expectRow(3, 2, "b", 1)
                .expectRow(4, 0, "d", 1);
    }

    @Test
    public void cols_ByName_StrExp() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols("X").merge("rowNum()");

        new DataFrameAsserts(df, "a", "b", "X")
                .expectHeight(5)
                .expectRow(0, 1, "a", 1)
                .expectRow(1, 1, "c", 2)
                .expectRow(2, 1, "e", 3)
                .expectRow(3, 2, "b", 1)
                .expectRow(4, 0, "d", 1);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols(1, 2).merge(rowNum(), $val("c"));

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(5)
                .expectRow(0, 1, 1, "c")
                .expectRow(1, 1, 2, "c")
                .expectRow(2, 1, 3, "c")
                .expectRow(3, 2, 1, "c")
                .expectRow(4, 0, 1, "c");
    }

    @Test
    public void cols_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols(c -> c.startsWith("b")).merge(rowNum());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, 1, 1)
                .expectRow(1, 1, 2)
                .expectRow(2, 1, 3)
                .expectRow(3, 2, 1)
                .expectRow(4, 0, 1);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept("a").merge(rowNum());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, 1, 1)
                .expectRow(1, 1, 2)
                .expectRow(2, 1, 3)
                .expectRow(3, 2, 1)
                .expectRow(4, 0, 1);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept(0).merge(rowNum());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, 1, 1)
                .expectRow(1, 1, 2)
                .expectRow(2, 1, 3)
                .expectRow(3, 2, 1)
                .expectRow(4, 0, 1);
    }

    @Test
    public void colsExcept_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept(c -> c.startsWith("a")).merge(rowNum());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, 1, 1)
                .expectRow(1, 1, 2)
                .expectRow(2, 1, 3)
                .expectRow(3, 2, 1)
                .expectRow(4, 0, 1);
    }
}
