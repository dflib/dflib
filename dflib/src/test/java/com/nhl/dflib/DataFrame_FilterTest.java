package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_FilterTest {

    @Test
    public void testFilterByColumn_Name() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(10, 20)
                .filterRows("a", (Integer v) -> v > 15);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testFilterByColumn_Pos() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(10, 20)
                .filterRows(0, (Integer v) -> v > 15);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testFilter_WithBooleanSeries() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(10, 20, 30)
                .filterRows(BooleanSeries.forBooleans(true, false, true));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 10)
                .expectRow(1, 30);
    }
}
