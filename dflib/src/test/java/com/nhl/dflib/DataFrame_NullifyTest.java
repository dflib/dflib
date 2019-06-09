package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_NullifyTest {

    @Test
    public void testNullify() {
        DataFrame cond = DataFrame
                .newFrame("a", "b")
                .columns(BooleanSeries.forBooleans(true, false), BooleanSeries.forBooleans(true, false));

        DataFrame df = DataFrame.newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .nullify(cond);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, null, null)
                .expectRow(1, 2, "y");
    }

    @Test
    public void testNullifyNoMatch() {
        DataFrame cond = DataFrame
                .newFrame("a", "b")
                .columns(BooleanSeries.forBooleans(true, false), BooleanSeries.forBooleans(true, false));

        DataFrame df = DataFrame.newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y").nullifyNoMatch(cond);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, null, null);
    }

    @Test
    public void testNullifyByColumn() {

        DataFrame cond = DataFrame
                .newFrame("c", "b")
                .columns(BooleanSeries.forBooleans(true, false), BooleanSeries.forBooleans(true, false));

        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .nullify(cond);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, null)
                .expectRow(1, 2, "y");
    }
}
