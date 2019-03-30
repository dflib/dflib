package com.nhl.dflib;

import org.junit.Test;

public class DataFrame_FilterTest extends BaseDataFrameTest {

    @Test
    public void testFilterByColumn_Name() {

        Index i1 = Index.forLabels("a");
        DataFrame df = createDf(i1, 10, 20)
                .filter("a", (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testFilterByColumn_Pos() {

        Index i1 = Index.forLabels("a");
        DataFrame df = createDf(i1, 10, 20)
                .filter(0, (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }
}
