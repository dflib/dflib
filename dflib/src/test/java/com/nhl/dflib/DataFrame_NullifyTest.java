package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_NullifyTest {

    @Test
    public void nullify() {
        DataFrame cond = DataFrame
                .newFrame("a", "b")
                .columns(Series.ofBool(true, false), Series.ofBool(true, false));

        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(
                        1, "x",
                        2, "y")
                .nullify(cond);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, null, null)
                .expectRow(1, 2, "y");
    }

    @Test
    public void nullifyNoMatch() {
        DataFrame cond = DataFrame
                .newFrame("a", "b")
                .columns(Series.ofBool(true, false), Series.ofBool(true, false));

        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(
                        1, "x",
                        2, "y").nullifyNoMatch(cond);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, null, null);
    }

    @Test
    public void nullifyByColumn() {

        DataFrame cond = DataFrame
                .newFrame("c", "b")
                .columns(Series.ofBool(true, false), Series.ofBool(true, false));

        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .nullify(cond);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, null)
                .expectRow(1, 2, "y");
    }
}
