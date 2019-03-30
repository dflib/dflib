package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class DataFrame_GetColumnTest {

    @Test
    public void testGetColumn_byLabel() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByRow(i1,
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn("b");

        new SeriesAsserts(cb).expectData("x", "y");
    }

    @Test
    public void testGetColumn_byPosition() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrameFactory.forSequenceFoldByRow(i1,
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn(0);

        new SeriesAsserts(cb).expectData(1, 2);
    }
}
