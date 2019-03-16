package com.nhl.dflib.row;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.row.IterableRowDataFrame;
import com.nhl.dflib.row.MaterializableRowDataFrame;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class MaterializableRowDataFrameTest {

    @Test
    public void testConstructor_SparseDF() {

        IndexPosition b = new IndexPosition(0, 1, "b");
        Index sparseIndex = Index.withPositions(b);
        assertFalse(sparseIndex.isCompact());

        DataFrame df1 = new IterableRowDataFrame(sparseIndex, asList(
                DataFrame.row(0, 1),
                DataFrame.row(2, 3)
        ));

        DataFrame df = new MaterializableRowDataFrame(df1);

        new DFAsserts(df, b)
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 3);
    }

}
