package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrameConditionsTest {

    @Test
    public void testEq1() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame eq = df1.eq(df2);

        new DFAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, true, true)
                .expectRow(1, true, true);
    }

    @Test
    public void testEq2() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                1, "X",
                2, "y");

        DataFrame eq = df1.eq(df2);

        new DFAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, true, false)
                .expectRow(1, true, true);
    }

    @Test
    public void testNe1() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame eq = df1.ne(df2);

        new DFAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, false, false)
                .expectRow(1, false, false);
    }

    @Test
    public void testNe2() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "Y");

        DataFrame eq = df1.ne(df2);

        new DFAsserts(eq, "a", "b")
                .expectHeight(2)
                .expectRow(0, false, false)
                .expectRow(1, false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEq_ColMismatch() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "B").foldByRow(
                1, "x",
                2, "Y");

        df1.ne(df2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEq_RowsMismatch() {

        DataFrame df1 = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.newFrame("a", "b").foldByRow(
                2, "Y");

        df1.ne(df2);
    }
}
