package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class Join_PositionalTest {

    @Test
    public void inner() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(10, 20);

        DataFrame df = df1.innerJoin(df2).positional().select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 0, 1, 10, 20);
    }

    @Test
    public void inner_Reversed() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(10, 20);

        DataFrame df = df2.innerJoin(df1).positional().select();

        new DataFrameAsserts(df, "c", "d", "a", "b")
                .expectHeight(1)
                .expectRow(0, 10, 20, 0, 1);
    }

    @Test
    public void left() {

        DataFrame df1 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df2 = DataFrame.foldByRow("b").of(10);

        DataFrame df = df1.leftJoin(df2).positional().select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 0, 10)
                .expectRow(1, 1, null);
    }

    @Test
    public void left_RightLonger() {

        DataFrame df1 = DataFrame.foldByRow("b").of(10);

        DataFrame df2 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df = df1.leftJoin(df2).positional().select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(1)
                .expectRow(0, 10, 0);
    }

    @Test
    public void right() {

        DataFrame df1 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df2 = DataFrame.foldByRow("b").of(10);

        DataFrame df = df1.rightJoin(df2).positional().select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 0, 10);
    }

    @Test
    public void right_RightLonger() {

        DataFrame df1 = DataFrame.foldByRow("b").of(10);

        DataFrame df2 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df = df1.rightJoin(df2).positional().select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, 10, 0)
                .expectRow(1, null, 1);
    }

    @Test
    public void full_LeftLonger() {

        DataFrame df1 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df2 = DataFrame.foldByRow("b").of(10);

        DataFrame df = df1.fullJoin(df2).positional().select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 0, 10)
                .expectRow(1, 1, null);
    }

    @Test
    public void full_RightLonger() {

        DataFrame df1 = DataFrame.foldByRow("b").of(10);

        DataFrame df2 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df = df1.fullJoin(df2).positional().select();

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, 10, 0)
                .expectRow(1, null, 1);
    }
}
