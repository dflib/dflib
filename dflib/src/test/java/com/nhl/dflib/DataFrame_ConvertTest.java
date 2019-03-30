package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_ConvertTest extends BaseDataFrameTest {

    @Test
    public void testConvertColumn() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .convertColumn("a", v -> ((int) v) * 10);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

}

