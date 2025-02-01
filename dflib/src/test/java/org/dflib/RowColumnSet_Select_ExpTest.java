package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowColumnSet_Select_ExpTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .cols("b", "a")
                .select(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(3)
                .expectRow(0, "xa", 3)
                .expectRow(1, "yb", 6)
                .expectRow(2, "mn", -3);
    }

    @Test
    public void byIndex() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofInt(0, 2))
                .cols("b", "a")
                .select(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );


        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "xa", 3)
                .expectRow(1, "mn", -3);
    }

    @Test
    public void byIndex_AddRows_AddCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2)
                .cols("b", "a", "x")
                .select(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3),
                        Exp.$val("X")
                );


        new DataFrameAsserts(df, "b", "a", "x")
                .expectHeight(3)
                .expectRow(0, "xa", 3, "X")
                .expectRow(1, "mn", -3, "X")
                .expectRow(2, "mn", -3, "X");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .cols("b", "a")
                .select(
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$int(0).mul(3)
                );

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "xa", 3)
                .expectRow(1, "mn", -3);
    }
}
