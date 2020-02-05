package com.nhl.dflib.test;

import com.nhl.dflib.DataFrame;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static org.junit.Assert.*;

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
    public void testExpectRows_NullVararg() {

        DataFrame df = DataFrame.newFrame("a").foldByRow(null, null);
        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, (Object[]) null)
                .expectRow(1, (Object[]) null);
    }

    @Test
    public void testExpectRows_ByteArray() {

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(new byte[]{3, 4, 5}, new byte[]{}, null);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, new Object[]{new byte[]{3, 4, 5}})
                .expectRow(1, new Object[]{new byte[]{}})
                .expectRow(2, (Object) null);
    }

    @Test(expected = AssertionError.class)
    public void testExpectRows_ArryaTypeMismatch() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(new int[]{3, 4, 5});
        new DataFrameAsserts(df, "a").expectRow(0, new Object[]{new long[]{3, 4, 5}});
    }

    @Test
    public void testExpectRows_Mismatch() {

        DataFrame df = DataFrame.newFrame("a").foldByRow("a", "b");

        try {
            new DataFrameAsserts(df, "a")
                    .expectRow(0, "a")
                    .expectRow(1, "c");

            throw new RuntimeException("Must have failed comparision");
        } catch (ComparisonFailure f) {
            assertEquals("c", f.getExpected());
            assertEquals("b", f.getActual());
        }
    }

    @Test
    public void testExpectRows_Assert() {
        DataFrame df = DataFrame.newFrame("c1").foldByRow("a", "b");
        new DataFrameAsserts(df, "c1")
                .expectHeight(2)
                .expectRow(0, (v) -> assertEquals("a", v))
                .expectRow(1, (v) -> assertEquals("b", v));
    }

    @Test
    public void testExpectRows_nullColumnAssert() {
        DataFrame df = DataFrame.newFrame("c1", "c2").foldByRow("a", "b", "c", "d");
        new DataFrameAsserts(df, "c1", "c2")
                .expectHeight(2)
                .expectRow(0, (v) -> assertEquals("a", v), null)
                .expectRow(1, null, (v) -> assertEquals("d", v));
    }

    @Test
    public void testExpectRows_nullRowAssert() {
        DataFrame df = DataFrame.newFrame("c1", "c2").foldByRow("a", "b", "c", "d");
        try {
        new DataFrameAsserts(df, "c1", "c2")
                .expectHeight(2)
                .expectRow(0, null);
            throw new RuntimeException("Non null parameter is required");
        } catch (NullPointerException e) {
            //Do nothing
        }
    }

    @Test
    public void testExpectHeight_Mismatch() {

        DataFrame df = DataFrame.newFrame("a").foldByRow("a", "b");

        try {
            new DataFrameAsserts(df, "a").expectHeight(3);
            throw new RuntimeException("Must have failed comparision");
        } catch (AssertionError f) {
            assertEquals("Unexpected DataFrame height expected:<3> but was:<2>", f.getMessage());
        }
    }
}
