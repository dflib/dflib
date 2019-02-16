package com.nhl.dflib;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class MaterializableDataFrameTest {

    @Test
    public void testConstructor_SparseDF() {

        IndexPosition b = new IndexPosition(0, 1, "b");
        Index sparseIndex = Index.withPositions(b);
        assertFalse(sparseIndex.isCompact());

        DataFrame df1 = new SimpleDataFrame(sparseIndex, asList(
                DataFrame.row(0, 1),
                DataFrame.row(2, 3)
        ));

        DataFrame df = new MaterializableDataFrame(df1);

        new DFAsserts(df, b)
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 3);
    }

}
