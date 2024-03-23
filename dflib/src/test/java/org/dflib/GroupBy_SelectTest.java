package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;

public class GroupBy_SelectTest {

    @Test
    public void cols_Implicit() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").select();

        // must be sorted by groups in the order they are encountered
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "x")
                .expectRow(3, 2, "y")
                .expectRow(4, 0, "a");
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
                .select();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 2, "y")
                .expectRow(3, 0, "a");

        DataFrame df3 = df1.group("a")
                .head(1)
                .select();

        new DataFrameAsserts(df3, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 0, "a");
    }

    @Test
    public void head_Sort() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df2 = df1.group("a")
                .sort("b", false)
                .head(2)
                .select();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "y")
                .expectRow(1, 1, "x")
                .expectRow(2, 2, "y")
                .expectRow(3, 0, "a");
    }


    @Test
    public void tail() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "z");

        DataFrame df2 = df1.group("a")
                .tail(2)
                .select();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "y")
                .expectRow(1, 1, "z")
                .expectRow(2, 2, "y")
                .expectRow(3, 0, "a");

        DataFrame df3 = df1.group("a")
                .tail(1)
                .select();

        new DataFrameAsserts(df3, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "z")
                .expectRow(1, 2, "y")
                .expectRow(2, 0, "a");
    }

    @Test
    public void sort_Pos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "b",
                2, "a",
                1, "z",
                0, "n",
                1, "y");

        DataFrame df2 = df1.group("a")
                .sort(1, true)
                .select();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "z")
                .expectRow(3, 2, "a")
                .expectRow(4, 2, "b")
                .expectRow(5, 0, "n");
    }

    @Test
    public void sort_Name() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "b",
                2, "a",
                1, "z",
                0, "n",
                1, "y");

        DataFrame df2 = df1.group("a")
                .sort("b", true)
                .select();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "z")
                .expectRow(3, 2, "a")
                .expectRow(4, 2, "b")
                .expectRow(5, 0, "n");
    }

    @Test
    public void sort_Names() {
        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 2,
                2, "b", 1,
                2, "a", 2,
                1, "z", -1,
                0, "n", 5,
                1, "x", 1);

        DataFrame df2 = df1.group("a")
                .sort(new String[]{"b", "c"}, new boolean[]{true, true})
                .select();

        new DataFrameAsserts(df2, "a", "b", "c")
                .expectHeight(6)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 1, "x", 2)
                .expectRow(2, 1, "z", -1)
                .expectRow(3, 2, "a", 2)
                .expectRow(4, 2, "b", 1)
                .expectRow(5, 0, "n", 5);
    }

    @Test
    public void sort_Sorters() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "b",
                2, "a",
                1, "z",
                0, "n",
                1, "y");

        DataFrame df2 = df1.group("a")
                .sort($col(1).asc())
                .select();

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "x")
                .expectRow(1, 1, "y")
                .expectRow(2, 1, "z")
                .expectRow(3, 2, "a")
                .expectRow(4, 2, "b")
                .expectRow(5, 0, "n");
    }

    @Test
    public void cols_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols("b").select();

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, "a")
                .expectRow(1, "c")
                .expectRow(2, "e")
                .expectRow(3, "b")
                .expectRow(4, "d");
    }

    @Test
    public void cols_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols(1).select();

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, "a")
                .expectRow(1, "c")
                .expectRow(2, "e")
                .expectRow(3, "b")
                .expectRow(4, "d");
    }

    @Test
    public void cols_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").cols(c -> c.startsWith("b")).select();

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, "a")
                .expectRow(1, "c")
                .expectRow(2, "e")
                .expectRow(3, "b")
                .expectRow(4, "d");
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept("a").select();

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, "a")
                .expectRow(1, "c")
                .expectRow(2, "e")
                .expectRow(3, "b")
                .expectRow(4, "d");
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept(0).select();

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, "a")
                .expectRow(1, "c")
                .expectRow(2, "e")
                .expectRow(3, "b")
                .expectRow(4, "d");
    }

    @Test
    public void colsExcept_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "a",
                2, "b",
                1, "c",
                0, "d",
                1, "e");

        DataFrame df = df1.group("a").colsExcept(c -> c.startsWith("a")).select();

        new DataFrameAsserts(df, "b")
                .expectHeight(5)
                .expectRow(0, "a")
                .expectRow(1, "c")
                .expectRow(2, "e")
                .expectRow(3, "b")
                .expectRow(4, "d");
    }
}
