package com.nhl.dflib;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataFrame_ShapeTest extends BaseDataFrameTest {

    @Test
    public void testWidth() {
        Index i = Index.withLabels("a");
        DataFrame df = createDf(i,
                1,
                2
        );

        assertEquals(1, df.width());
    }

    @Test
    public void testHeight() {
        Index i = Index.withLabels("a");
        DataFrame df = createDf(i,
                1,
                2
        );

        assertEquals(2, df.height());
    }
}
