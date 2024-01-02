package org.dflib.junit5;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrameAssertsTest {

    @Test
    public void expectRows_String() {

        DataFrame df = DataFrame.foldByRow("a").of("a", "b");
        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, "a")
                .expectRow(1, "b");
    }

    @Test
    public void expectRows_Nulls() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(null, null, null, null);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, null, null)
                .expectRow(1, null, null);
    }

    @Test
    public void expectRows_NullVararg() {

        DataFrame df = DataFrame.foldByRow("a").of(null, null);
        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, (Object[]) null)
                .expectRow(1, (Object[]) null);
    }

    @Test
    public void expectRows_ByteArray() {

        DataFrame df = DataFrame.foldByRow("a").of(new byte[]{3, 4, 5}, new byte[]{}, null);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, new Object[]{new byte[]{3, 4, 5}})
                .expectRow(1, new Object[]{new byte[]{}})
                .expectRow(2, (Object) null);
    }

    @Test
    public void expectRows_ArryaTypeMismatch() {
        DataFrame df = DataFrame.foldByRow("a").of(new int[]{3, 4, 5});
        DataFrameAsserts asserts = new DataFrameAsserts(df, "a");
        assertThrows(AssertionError.class, () -> asserts.expectRow(0, new Object[]{new long[]{3, 4, 5}}));
    }

    @Test
    public void expectRows_Mismatch() {

        DataFrame df = DataFrame.foldByRow("a").of("a", "b");

        try {
            new DataFrameAsserts(df, "a")
                    .expectRow(0, "a")
                    .expectRow(1, "c");

            throw new RuntimeException("Must have failed comparision");
        } catch (AssertionFailedError f) {
            assertEquals("c", f.getExpected().getValue());
            assertEquals("b", f.getActual().getValue());
        }
    }

    @Test
    public void assertRows() {
        DataFrame df = DataFrame.foldByRow("c1").of("a", "b");
        new DataFrameAsserts(df, "c1")
                .expectHeight(2)
                .assertRow(0, (v) -> assertEquals("a", v))
                .assertRow(1, (String v) -> assertEquals("b", v));
    }

    @Test
    public void assertRows_MissedColumnAssert() {
        DataFrame df = DataFrame.foldByRow("c1", "c2").of("a", "b", "c", "d");
        try {
            new DataFrameAsserts(df, "c1", "c2")
                    .expectHeight(2)
                    .assertRow(0, (v) -> assertEquals("a", v));
            throw new RuntimeException("Must be failure due to missed column assertion");
        } catch (AssertionError e) {
            assertEquals("The number of assert arguments must be equal to the number of DataFrame columns. ==> expected: <2> but was: <1>",
                    e.getMessage());
        }
    }

    @Test
    public void assertRows_nullColumnAssert() {
        DataFrame df = DataFrame.foldByRow("c1", "c2").of("a", "b", "c", "d");

        DataFrameAsserts asserts = new DataFrameAsserts(df, "c1", "c2").expectHeight(2);

        assertThrows(NullPointerException.class,
                () -> asserts
                        .assertRow(0, (v) -> assertEquals("a", v), null)
                        .assertRow(1, null, (v) -> assertEquals("d", v)),
                "Must be failure due to null column assertion");
    }

    @Test
    public void assertRows_nullRowAssert() {
        DataFrame df = DataFrame.foldByRow("c1", "c2").of("a", "b", "c", "d");

        DataFrameAsserts asserts = new DataFrameAsserts(df, "c1", "c2").expectHeight(2);

        assertThrows(NullPointerException.class,
                () -> asserts.assertRow(0, null),
                "Non null parameter is required");
    }

    @Test
    public void expectHeight_Mismatch() {

        DataFrame df = DataFrame.foldByRow("a").of("a", "b");

        try {
            new DataFrameAsserts(df, "a").expectHeight(3);
            throw new RuntimeException("Must have failed comparision");
        } catch (AssertionError f) {
            assertEquals("Unexpected DataFrame height ==> expected: <3> but was: <2>", f.getMessage());
        }
    }
}
