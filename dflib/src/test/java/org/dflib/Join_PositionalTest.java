package org.dflib;

import org.dflib.join.JoinIndicator;
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

    @Test
    public void cols_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                10, 20,
                30, 40);

        DataFrame df = df1.innerJoin(df2).positional()
                .cols("b", "c")
                .select();

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 3, 30);
    }

    @Test
    public void colsExcept_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                10, 20,
                30, 40);

        DataFrame df = df1.innerJoin(df2).positional()
                .colsExcept("a", "d")
                .select();

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 3, 30);
    }

    @Test
    public void indicatorColumn() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3,
                4, 5);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                10, 20);

        DataFrame df = df1.fullJoin(df2).positional()
                .indicatorColumn("ind")
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d", "ind")
                .expectHeight(3)
                .expectRow(0, 0, 1, 10, 20, JoinIndicator.both)
                .expectRow(1, 2, 3, null, null, JoinIndicator.left_only)
                .expectRow(2, 4, 5, null, null, JoinIndicator.left_only);
    }

    @Test
    public void indicatorColumn_WithCols() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3,
                4, 5);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                10, 20);

        DataFrame df = df1.fullJoin(df2).positional()
                .indicatorColumn("ind")
                .cols("a", "ind", "d")
                .select();

        new DataFrameAsserts(df, "a", "ind", "d")
                .expectHeight(3)
                .expectRow(0, 0, JoinIndicator.both, 20)
                .expectRow(1, 2, JoinIndicator.left_only, null)
                .expectRow(2, 4, JoinIndicator.left_only, null);
    }

    @Test
    public void indicatorColumn_RightOnly() {

        DataFrame df1 = DataFrame.foldByRow("a").of(10);

        DataFrame df2 = DataFrame.foldByRow("b").of(
                0,
                1,
                2);

        DataFrame df = df1.fullJoin(df2).positional()
                .indicatorColumn("ind")
                .select();

        new DataFrameAsserts(df, "a", "b", "ind")
                .expectHeight(3)
                .expectRow(0, 10, 0, JoinIndicator.both)
                .expectRow(1, null, 1, JoinIndicator.right_only)
                .expectRow(2, null, 2, JoinIndicator.right_only);
    }
}
