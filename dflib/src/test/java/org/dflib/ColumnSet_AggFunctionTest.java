package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;

public class ColumnSet_AggFunctionTest {

    @Test
    public void test() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols().agg($col(1).agg(Series::size));

        new DataFrameAsserts(agg, "b")
                .expectHeight(1)
                .expectRow(0, 2);
    }
}
