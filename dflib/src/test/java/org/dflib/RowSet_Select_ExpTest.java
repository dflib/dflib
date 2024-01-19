package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Select_ExpTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }

    @Test
    public void byPos_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 0, 2)
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n")
                .expectRow(2, 3, "xa", "a")
                .expectRow(3, -3, "mn", "n");
    }

    @Test
    public void byIndex_Empty() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt())
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void byIndex_Expand_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2, 0)
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n")
                .expectRow(2, -3, "mn", "n")
                .expectRow(3, 3, "xa", "a");
    }

    @Test
    public void byRange() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRangeOpenClosed(1, 2)
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(1)
                .expectRow(0, 6, "yb", "b");
    }

    @Test
    public void byRange_Empty() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rowsRangeOpenClosed(1, 1)
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }

    @Test
    public void byConditionExp() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Exp.$int("a").mod(2).ne(0))
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }

    @Test
    public void byRowPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(r -> r.getInt(0) % 2 != 0)
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, -3, "mn", "n");
    }

    @Test
    public void byRowPredicate_All() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .compactInt(0, 0)
                .rows(r -> true)
                .select(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 6, "yb", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
