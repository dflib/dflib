package com.nhl.dflib.unit;

import com.nhl.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrameAssertsTest {

    @Test
    public void expectRows_String() {

        DataFrame df = DataFrame.foldByRow("a").of("a", "b", null);
        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, "a")
                .expectRow(1, "b")
                .expectRow(2, (Object) null);
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
    public void expectRows_ArrayTypeMismatch() {
        DataFrame df = DataFrame.foldByRow("a").of(new int[]{3, 4, 5});
        assertThrows(AssertionError.class, () ->  new DataFrameAsserts(df, "a").expectRow(0, new Object[]{new long[]{3, 4, 5}}));
    }
}
