package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_FilterTest {

    @Test
    public void testFilterByColumn_Name() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(10, 20)
                .filter("a", (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testFilterByColumn_Pos() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(10, 20)
                .filter(0, (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }
}
