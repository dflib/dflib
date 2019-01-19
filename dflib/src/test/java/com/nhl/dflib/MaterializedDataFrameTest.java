package com.nhl.dflib;

import org.junit.Test;

public class MaterializedDataFrameTest {

    @Test
    public void testConstructor_SparseDF() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.fromSequence(i1,
                0, 1,
                2, 3).selectColumns("b");

        DataFrame df = new MaterializedDataFrame(df1);

        new DFAsserts(df, new IndexPosition(0, 1, "b"))
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 3);
    }

}
