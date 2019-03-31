package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_TailTest extends BaseDataFrameTest {

    @Test
    public void testTail() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y",
                3, "z")
                .tail(2);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 2, "y")
                .expectRow(1, 3, "z");
    }

    @Test
    public void testTail_Zero() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y",
                3, "z")
                .tail(0);

        new DFAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void testTail_OutOfBounds() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y",
                3, "z")
                .tail(4);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");
    }
}
