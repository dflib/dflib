package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrameConditionsTest extends BaseDataFrameTest {

    @Test
    public void testEq1() {

        Index i1 = Index.forLabels("a", "b");
        Index i2 = Index.forLabels("a", "b");

        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        DataFrame df2 = createDf(i2,
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

        Index i1 = Index.forLabels("a", "b");
        Index i2 = Index.forLabels("a", "b");

        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        DataFrame df2 = createDf(i2,
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

        Index i1 = Index.forLabels("a", "b");
        Index i2 = Index.forLabels("a", "b");

        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        DataFrame df2 = createDf(i2,
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

        Index i1 = Index.forLabels("a", "b");
        Index i2 = Index.forLabels("a", "b");

        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        DataFrame df2 = createDf(i2,
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

        Index i1 = Index.forLabels("a", "b");
        Index i2 = Index.forLabels("a", "B");

        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        DataFrame df2 = createDf(i2,
                1, "x",
                2, "Y");

        df1.ne(df2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEq_RowsMismatch() {

        Index i1 = Index.forLabels("a", "b");
        Index i2 = Index.forLabels("a", "b");

        DataFrame df1 = createDf(i1,
                1, "x",
                2, "y");

        DataFrame df2 = createDf(i2,
                2, "Y");

        df1.ne(df2);
    }
}
