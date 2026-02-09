package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

@Deprecated
public class DataFrame_HConcat_Test {

    @Test
    public void zipRows_ImplicitInnerJoin() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                10, 20);

        DataFrame df_l = df1.hConcat(df2);

        new DataFrameAsserts(df_l, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 0, 1, 10, 20);

        DataFrame df_r = df2.hConcat(df1);

        new DataFrameAsserts(df_r, "c", "d", "a", "b")
                .expectHeight(1)
                .expectRow(0, 10, 20, 0, 1);
    }

    @Test
    public void zipRows_OneColumnDF() {
        DataFrame df1 = DataFrame.foldByRow("b").of(1, 3);
        DataFrame df2 = DataFrame.foldByRow("c").of(10, 30);

        DataFrame df = df1.hConcat(df2);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 3, 30);
    }

    @Test
    public void zipRows_SparseDF_CustomIndex() {

        DataFrame df1 = DataFrame.foldByRow("b").of(1, 3);
        DataFrame df2 = DataFrame.foldByRow("c").of(10, 30);

        DataFrame df = df1.hConcat(Index.of("x", "y"), JoinType.inner, df2, RowCombiner.zip(df1.width()));

        new DataFrameAsserts(df, "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 3, 30);
    }

    @Test
    public void zipRows_SparseDF_ReorgColumns() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                10, 20,
                30, 40,
                50, 60);

        RowCombiner c = (lr, rr, tr) -> {

            if (rr != null) {
                tr.set(0, rr.get(1));
            }

            if (lr != null) {
                tr.set(1, lr.get(0));
            }
        };

        DataFrame df = df1.hConcat(Index.of("x", "y"), JoinType.right, df2, c);

        new DataFrameAsserts(df, "x", "y")
                .expectHeight(3)
                .expectRow(0, 20, 0)
                .expectRow(1, 40, 2)
                .expectRow(2, 60, null);
    }

    @Test
    public void zipRows_InnerJoin() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                0, 1,
                2, 3);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(10, 20);

        DataFrame df_l = df1.hConcat(JoinType.inner, df2);

        new DataFrameAsserts(df_l, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 0, 1, 10, 20);

        DataFrame df_r = df2.hConcat(df1);

        new DataFrameAsserts(df_r, "c", "d", "a", "b")
                .expectHeight(1)
                .expectRow(0, 10, 20, 0, 1);
    }

    @Test
    public void zipRows_Left() {

        DataFrame df1 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df2 = DataFrame.foldByRow("b").of(10);

        DataFrame df_l = df1.hConcat(JoinType.left, df2);

        new DataFrameAsserts(df_l, "a", "b")
                .expectHeight(2)
                .expectRow(0, 0, 10)
                .expectRow(1, 1, null);

        DataFrame df_r = df2.hConcat(JoinType.left, df1);

        new DataFrameAsserts(df_r, "b", "a")
                .expectHeight(1)
                .expectRow(0, 10, 0);
    }

    @Test
    public void zipRows_Right() {

        DataFrame df1 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df2 = DataFrame.foldByRow("b").of(10);

        DataFrame df_l = df1.hConcat(JoinType.right, df2);
        new DataFrameAsserts(df_l, "a", "b")
                .expectHeight(1)
                .expectRow(0, 0, 10);


        DataFrame df_r = df2.hConcat(JoinType.right, df1);
        new DataFrameAsserts(df_r, "b", "a")
                .expectHeight(2)
                .expectRow(0, 10, 0)
                .expectRow(1, null, 1);
    }

    @Test
    public void zipRows_Full() {

        DataFrame df1 = DataFrame.foldByRow("a").of(
                0,
                1);

        DataFrame df2 = DataFrame.foldByRow("b").of(10);

        DataFrame df_l = df1.hConcat(JoinType.full, df2);
        new DataFrameAsserts(df_l, "a", "b")
                .expectHeight(2)
                .expectRow(0, 0, 10)
                .expectRow(1, 1, null);


        DataFrame df_r = df2.hConcat(JoinType.full, df1);
        new DataFrameAsserts(df_r, "b", "a")
                .expectHeight(2)
                .expectRow(0, 10, 0)
                .expectRow(1, null, 1);
    }
}
