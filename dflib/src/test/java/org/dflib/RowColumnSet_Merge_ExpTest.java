package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RowColumnSet_Merge_ExpTest {

    @Test
    public void rowsAll_colsDeferred() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows().cols()
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)).as("a"),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c", "a * 3")
                .expectHeight(3)
                .expectRow(0, "xa", "x", "a", 3)
                .expectRow(1, "yb", "y", "b", 6)
                .expectRow(2, "mn", "m", "n", -3);
    }

    @Test
    public void rowsAll_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows().cols("b", "a")
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void colsByName_rowsAll() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols("b", "a").rows()
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsAll_colsByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows().cols(1, 0)
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByIndex_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2).cols("b", "a")
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void colsByName_rowsByIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .cols("b", "a").rows(0, 2)
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByIndex_colsByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2)).cols(1, 0)
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByIndex_colsDeferred() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2)).cols()
                .merge(
                        Exp.$int(0).mul(3).as("a"),
                        Exp.concat(Exp.$str(1), Exp.$str(2)).as("b")
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByIndex_colsPredicated() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2).cols(c -> List.of("a", "b").contains(c))
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "xa", 3, "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, "mn", -3, "n");
    }

    @Test
    public void rowsByIndex_colsByName_RepeatRows_ExpandCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(2, 0, 2)
                .cols("b", "a", "x")
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3),
                        Exp.rowNum()
                );

        new DataFrameAsserts(df, "a", "b", "c", "x")
                .expectHeight(4)
                .expectRow(0, 3, "xa", "a", 2)
                .expectRow(1, 2, "y", "b", null)
                .expectRow(2, -3, "mn", "n", 1)
                .expectRow(3, -3, "mn", "n", 3);
    }

    @Test
    public void rowsByIndex_colsByName_EmptyRows() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt()).cols("b", "a")
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -1, "m", "n");
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true)).cols("b", "a")
                .merge(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void rowsByCondition_colsByName_StrExp() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .cols()
                .merge("concat(str(1), str(2)) as b, int(0) * 3 as a");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
