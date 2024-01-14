package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RowColumnSet_Map_ExpTest {

    @Test
    public void allRows_NamedCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .cols("b", "a")
                .map(
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
    public void allRows_PosCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .cols(1, 0)
                .map(
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
    public void rowsByIndex_NamedCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .cols("b", "a")
                .map(
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
    public void rowsByIndex_PosCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .cols(1, 0)
                .map(
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
    public void rowsByIndex_PredicatedCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .cols(c -> List.of("a", "b").contains(c))
                .map(
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
    public void rowsByIndex_NamedCols_RepeatRows_ExpandCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(2, 0, 2)
                .cols("b", "a", "x")
                .map(
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
    public void rowsByIndex_NamedCols_EmptyRows() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt())
                .cols("b", "a")
                .map(
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
    public void rowsByCondition_NamedCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .cols("b", "a")
                .map(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
