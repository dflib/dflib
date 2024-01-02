package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_ValueCounts {

    @Test
    public void valueCounts() {
        DataFrame counts = Series.ofInt(1, 3, 1, 3, 1, 0).valueCounts();

        new DataFrameAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, 1, 3)
                .expectRow(1, 3, 2)
                .expectRow(2, 0, 1);
    }
}
