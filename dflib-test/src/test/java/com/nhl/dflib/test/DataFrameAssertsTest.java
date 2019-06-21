package com.nhl.dflib.test;

import com.nhl.dflib.DataFrame;
import org.junit.Test;

public class DataFrameAssertsTest {

    @Test
    public void testExpectRows_String() {

        DataFrame df = DataFrame.newFrame("a").foldByRow("a", "b");
        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, "a")
                .expectRow(1, "b");
    }

    @Test
    public void testExpectRows_Nulls() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(null, null, null, null);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, null, null)
                .expectRow(1, null, null);
    }

    @Test
    public void testExpectRows_Nulls_SingleColumn() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(null, null);
        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, null)
                .expectRow(1, null);
    }

    @Test
    public void testExpectRows_ByteArray() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(new byte[]{3, 4, 5}, new byte[]{}, null);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, new Object[]{new byte[]{3, 4, 5}})
                .expectRow(1, new Object[]{new byte[]{}})
                .expectRow(2, null);
    }

    @Test(expected = AssertionError.class)
    public void testExpectRows_ArryaTypeMismatch() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(new int[]{3, 4, 5});
        new DataFrameAsserts(df, "a").expectRow(0, new Object[]{new long[]{3, 4, 5}});
    }
}
