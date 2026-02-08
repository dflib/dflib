package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DataFrame_Union_Test {

    @Test
    public void union() {

        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("a").of(10, 20);

        DataFrame df = DataFrame.union(df1, df2);

        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10)
                .expectRow(3, 20);
    }

    @Test
    public void union_Multiple() {

        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("a").of(10);
        DataFrame df3 = DataFrame.foldByRow("a").of(20);

        DataFrame df = DataFrame.union(df1, df2, df3);

        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10)
                .expectRow(3, 20);
    }

    @Test
    public void defaultInner() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(df1, df2);

        new DataFrameAsserts(df, "b")
                .expectHeight(4)
                .expectRow(0, 2)
                .expectRow(1, 4)
                .expectRow(2, 20)
                .expectRow(3, 40);
    }

    @Test
    public void left() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.left, df1, df2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, null, 20)
                .expectRow(3, null, 40);
    }

    @Test
    public void right() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.right, df1, df2);

        new DataFrameAsserts(df, "c", "b")
                .expectHeight(4)
                .expectRow(0, null, 2)
                .expectRow(1, null, 4)
                .expectRow(2, 10, 20)
                .expectRow(3, 30, 40);
    }

    @Test
    public void inner_Multiple() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df3 = DataFrame.foldByRow("b", "d").of(
                100, 200,
                300, 400);

        DataFrame df = DataFrame.union(JoinType.inner, df1, df2, df3);

        new DataFrameAsserts(df, "b")
                .expectHeight(6)
                .expectRow(0, 2)
                .expectRow(1, 4)
                .expectRow(2, 20)
                .expectRow(3, 40)
                .expectRow(4, 100)
                .expectRow(5, 300);
    }

    @Test
    public void full() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.full, df1, df2);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, 2, null)
                .expectRow(1, 3, 4, null)
                .expectRow(2, null, 20, 10)
                .expectRow(3, null, 40, 30);
    }

    @Test
    public void union_Iterable() {

        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("a").of(10, 20);

        DataFrame df = DataFrame.union(List.of(df1, df2));

        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10)
                .expectRow(3, 20);
    }

    @Test
    public void union_Iterable_Multiple() {

        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("a").of(10);
        DataFrame df3 = DataFrame.foldByRow("a").of(20);

        DataFrame df = DataFrame.union(List.of(df1, df2, df3));

        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10)
                .expectRow(3, 20);
    }

    @Test
    public void union_Iterable_Inner() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.inner, List.of(df1, df2));

        new DataFrameAsserts(df, "b")
                .expectHeight(4)
                .expectRow(0, 2)
                .expectRow(1, 4)
                .expectRow(2, 20)
                .expectRow(3, 40);
    }

    @Test
    public void union_Iterable_Left() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.left, List.of(df1, df2));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, null, 20)
                .expectRow(3, null, 40);
    }

    @Test
    public void union_Iterable_Right() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.right, List.of(df1, df2));

        new DataFrameAsserts(df, "c", "b")
                .expectHeight(4)
                .expectRow(0, null, 2)
                .expectRow(1, null, 4)
                .expectRow(2, 10, 20)
                .expectRow(3, 30, 40);
    }

    @Test
    public void union_Iterable_Full() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df = DataFrame.union(JoinType.full, List.of(df1, df2));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, 2, null)
                .expectRow(1, 3, 4, null)
                .expectRow(2, null, 20, 10)
                .expectRow(3, null, 40, 30);
    }

    @Test
    public void left_Multiple() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df3 = DataFrame.foldByRow("b", "d").of(
                100, 200,
                300, 400);

        DataFrame df = DataFrame.union(JoinType.left, df1, df2, df3);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, null, 20)
                .expectRow(3, null, 40)
                .expectRow(4, null, 100)
                .expectRow(5, null, 300);
    }

    @Test
    public void right_Multiple() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df3 = DataFrame.foldByRow("b", "d").of(
                100, 200,
                300, 400);

        DataFrame df = DataFrame.union(JoinType.right, df1, df2, df3);

        new DataFrameAsserts(df, "b", "d")
                .expectHeight(6)
                .expectRow(0, 2, null)
                .expectRow(1, 4, null)
                .expectRow(2, 20, null)
                .expectRow(3, 40, null)
                .expectRow(4, 100, 200)
                .expectRow(5, 300, 400);
    }

    @Test
    public void full_Multiple() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "b").of(
                10, 20,
                30, 40);

        DataFrame df3 = DataFrame.foldByRow("b", "d").of(
                100, 200,
                300, 400);

        DataFrame df = DataFrame.union(JoinType.full, df1, df2, df3);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(6)
                .expectRow(0, 1, 2, null, null)
                .expectRow(1, 3, 4, null, null)
                .expectRow(2, null, 20, 10, null)
                .expectRow(3, null, 40, 30, null)
                .expectRow(4, null, 100, null, 200)
                .expectRow(5, null, 300, null, 400);
    }

    @Test
    public void union_SingleDataFrame() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df = DataFrame.union(df1);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void union_Iterable_SingleDataFrame() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df = DataFrame.union(List.of(df1));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void union_WithEmptyDataFrame() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("a", "b").of();

        DataFrame df = DataFrame.union(df1, df2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void union_EmptyDataFrameFirst() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of();

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df = DataFrame.union(df1, df2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void union_AllEmptyDataFrames() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of();
        DataFrame df2 = DataFrame.foldByRow("a", "b").of();

        DataFrame df = DataFrame.union(df1, df2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void union_MixedEmptyAndNonEmpty() {

        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("a").of();
        DataFrame df3 = DataFrame.foldByRow("a").of(10);

        DataFrame df = DataFrame.union(df1, df2, df3);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10);
    }

    @Test
    public void union_EmptyWithDifferentColumns() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of();

        DataFrame df = DataFrame.union(JoinType.full, df1, df2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, 2, null, null)
                .expectRow(1, 3, 4, null, null);
    }

}
