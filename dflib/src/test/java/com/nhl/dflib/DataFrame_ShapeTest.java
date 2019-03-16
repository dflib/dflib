package com.nhl.dflib;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing DataFrame interface methods behavior, by not using factory methods that create subclasses that override
 * base methods.
 */
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
