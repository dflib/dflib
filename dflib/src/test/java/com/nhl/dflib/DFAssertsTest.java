package com.nhl.dflib;

import org.junit.Test;

public class DFAssertsTest {

    @Test
    public void testExpectRows_String() {

        DataFrame df = DataFrame.fromSequenceFoldByRow(Index.withNames("a"), "a", "b", null);
        new DFAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, "a")
                .expectRow(1, "b")
                .expectRow(2, new Object[]{null});
    }

    @Test
    public void testExpectRows_ByteArray() {

        DataFrame df = DataFrame.fromSequenceFoldByRow(Index.withNames("a"), new byte[]{3, 4, 5}, new byte[]{}, null);
        new DFAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, new Object[]{new byte[]{3, 4, 5}})
                .expectRow(1, new Object[]{new byte[]{}})
                .expectRow(2, new Object[]{null});
    }

    @Test(expected = AssertionError.class)
    public void testExpectRows_ArryaTypeMismatch() {

        DataFrame df = DataFrame.fromSequenceFoldByRow(Index.withNames("a"), new int[]{3, 4, 5});
        new DFAsserts(df, "a").expectRow(0, new Object[]{new long[]{3, 4, 5}});
    }
}
