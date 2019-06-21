package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

public class DataFrame_VConcat_Test {

    @Test
    public void testVConcat_Default() {

        DataFrame df1 = DataFrame.newFrame("a").foldByRow(1, 2);
        DataFrame df2 = DataFrame.newFrame("a").foldByRow(10, 20);

        DataFrame df = df1.vConcat(df2);

        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10)
                .expectRow(3, 20);
    }

    @Test
    public void testVConcat_Default_Multiple() {

        DataFrame df1 = DataFrame.newFrame("a").foldByRow(1, 2);
        DataFrame df2 = DataFrame.newFrame("a").foldByRow(10);
        DataFrame df3 = DataFrame.newFrame("a").foldByRow(20);

        DataFrame df = df1.vConcat(df2, df3);

        new DataFrameAsserts(df, "a")
                .expectHeight(4)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 10)
                .expectRow(3, 20);
    }

    @Test
    public void testVConcat_Default_Left() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.newFrame("c", "b").foldByRow(
                10, 20,
                30, 40);

        DataFrame df = df1.vConcat(df2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, null, 20)
                .expectRow(3, null, 40);
    }

    @Test
    public void testVConcat_Left() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.newFrame("c", "b").foldByRow(
                10, 20,
                30, 40);

        DataFrame df = df1.vConcat(JoinType.left, df2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4)
                .expectRow(2, null, 20)
                .expectRow(3, null, 40);
    }

    @Test
    public void testVConcat_Right() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.newFrame("c", "b").foldByRow(
                10, 20,
                30, 40);

        DataFrame df = df1.vConcat(JoinType.right, df2);

        new DataFrameAsserts(df, "c", "b")
                .expectHeight(4)
                .expectRow(0, null, 2)
                .expectRow(1, null, 4)
                .expectRow(2, 10, 20)
                .expectRow(3, 30, 40);
    }

    @Test
    public void testVConcat_Inner_Multiple() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.newFrame("c", "b").foldByRow(
                10, 20,
                30, 40);

        DataFrame df3 = DataFrame.newFrame("b", "d").foldByRow(
                100, 200,
                300, 400);

        DataFrame df = df1.vConcat(JoinType.inner, df2, df3);

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
    public void testVConcat_Full() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.newFrame("c", "b").foldByRow(
                10, 20,
                30, 40);

        DataFrame df = df1.vConcat(JoinType.full, df2);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, 1, 2, null)
                .expectRow(1, 3, 4, null)
                .expectRow(2, null, 20, 10)
                .expectRow(3, null, 40, 30);
    }

}
