package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnDataFrameTest {

    @Test
    public void constructor() {
        Index i = Index.of("a", "b");
        ColumnDataFrame df = new ColumnDataFrame(i,
                Series.ofInt(1, 2),
                Series.ofInt(3, 4));

        new DataFrameAsserts(df, i).expectHeight(2);
    }

    @Test
    public void constructor_NoData() {
        Index i = Index.of("a", "b");
        ColumnDataFrame df = new ColumnDataFrame(i);

        new DataFrameAsserts(df, i).expectHeight(0);
    }
}
