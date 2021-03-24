package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_PivotTest {

    @Test
    public void testPivot_Aggregation() {

        DataFrame df1 = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", 15.0,
                2, "y", 18.0,
                2, "y", 19.0,
                1, "y", 20.0);


        DataFrame df = df1.pivot().columns("b").rows("a").values("c", SeriesAggregator.sumDouble());

        new DataFrameAsserts(df, "a", "x", "y")
                .expectHeight(2)
                .expectRow(0, 1, 15.0, 20.0)
                .expectRow(1, 2, null, 37.0);
    }
}
