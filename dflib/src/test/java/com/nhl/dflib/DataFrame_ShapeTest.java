package com.nhl.dflib;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataFrame_ShapeTest {

    @Test
    public void testWidth() {
        Index i = Index.withNames("a");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i,
                1,
                2
        );

        assertEquals(1, df.width());
    }

    @Test
    public void testHeight() {
        Index i = Index.withNames("a");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i,
                1,
                2
        );

        assertEquals(2, df.height());

    }
}
