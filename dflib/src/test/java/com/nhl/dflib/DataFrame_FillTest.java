package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_FillTest extends BaseDataFrameTest {

    @Test
    public void testFillNulls() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame df = createDf(i1,
                "a", 1,
                null, 5,
                "b", null,
                null, null).fillNulls("A");

        new DFAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, "A", 5)
                .expectRow(2, "b", "A")
                .expectRow(3, "A", "A");
    }

    @Test
    public void testFillNulls_Column() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame df = createDf(i1,
                "a", 1,
                null, 5,
                "b", null,
                null, null).fillNulls("b", "A");

        new DFAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, null, 5)
                .expectRow(2, "b", "A")
                .expectRow(3, null, "A");
    }

    @Test
    public void testFillNullsFromSeries() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame df = createDf(i1,
                "a", 1,
                null, 5,
                "b", null,
                null, null).fillNullsFromSeries("b", Series.forData("Q", "R", "S", "T"));

        new DFAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, null, 5)
                .expectRow(2, "b", "S")
                .expectRow(3, null, "T");
    }

    @Test
    public void testFillNullsBackwards() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame df = createDf(i1,
                "a", null,
                null, 5,
                "b", null,
                "c", 8,
                null, null).fillNullsBackwards("b");

        new DFAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, "a", 5)
                .expectRow(1, null, 5)
                .expectRow(2, "b", 8)
                .expectRow(3, "c", 8)
                .expectRow(4, null, null);
    }

    @Test
    public void testFillNullsForward() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame df = createDf(i1,
                "a", null,
                null, 5,
                "b", null,
                "c", 8,
                null, null).fillNullsForward("b");

        new DFAsserts(df, "a", "b")
                .expectHeight(5)
                .expectRow(0, "a", null)
                .expectRow(1, null, 5)
                .expectRow(2, "b", 5)
                .expectRow(3, "c", 8)
                .expectRow(4, null, 8);
    }
}
