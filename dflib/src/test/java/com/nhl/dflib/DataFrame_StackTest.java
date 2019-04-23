package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_StackTest extends BaseDataFrameTest {

    @Test
    public void testStack() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                null, null,
                2, "y").stack();

        new DFAsserts(df, "row", "column", "value")
                .expectHeight(4)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 2, "a", 2)
                .expectRow(2, 0, "b", "x")
                .expectRow(3, 2, "b", "y");
    }

    @Test
    public void testStack_NoNulls() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                5, "z",
                2, "y").stack();

        new DFAsserts(df, "row", "column", "value")
                .expectHeight(6)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 1, "a", 5)
                .expectRow(2, 2, "a", 2)
                .expectRow(3, 0, "b", "x")
                .expectRow(4, 1, "b", "z")
                .expectRow(5, 2, "b", "y");
    }

    @Test
    public void testStackIncludeNulls() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                null, null,
                2, "y").stackIncludeNulls();

        new DFAsserts(df, "row", "column", "value")
                .expectHeight(6)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 1, "a", null)
                .expectRow(2, 2, "a", 2)
                .expectRow(3, 0, "b", "x")
                .expectRow(4, 1, "b", null)
                .expectRow(5, 2, "b", "y");
    }
}
