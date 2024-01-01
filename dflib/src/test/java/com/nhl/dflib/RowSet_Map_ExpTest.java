package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class RowSet_Map_ExpTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows()
                .map(
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
                .map(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }

    @Test
    public void byCondition() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true))
                .map(
                        Exp.$int(0).mul(3),
                        Exp.concat(Exp.$str(1), Exp.$str(2)),
                        Exp.$str(2)
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 3, "xa", "a")
                .expectRow(1, 2, "y", "b")
                .expectRow(2, -3, "mn", "n");
    }
}
