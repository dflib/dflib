package com.nhl.dflib.unit;

import com.nhl.dflib.DataFrame;
import org.junit.Test;

public class DataFrameAssertsTest {

    @Test
    public void testExpectRows_String() {

        DataFrame df = DataFrame.newFrame("a").foldByRow("a", "b", null);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, "a")
                .expectRow(1, "b")
                .expectRow(2, (Object) null);
    }

    @Test
    public void testExpectRows_ByteArray() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(new byte[]{3, 4, 5}, new byte[]{}, null);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, new Object[]{new byte[]{3, 4, 5}})
                .expectRow(1, new Object[]{new byte[]{}})
                .expectRow(2, (Object) null);
    }

    @Test(expected = AssertionError.class)
    public void testExpectRows_ArrayTypeMismatch() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(new int[]{3, 4, 5});
        new DataFrameAsserts(df, "a").expectRow(0, new Object[]{new long[]{3, 4, 5}});
    }
}
