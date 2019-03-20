package com.nhl.dflib;

import org.junit.Test;

public class IndexTest {

    @Test
    public void testWithNames_Enum() {
        Index i = Index.withNames(E1.class);

        // TODO: a test helper for Index
        new DFAsserts(DataFrame.fromRows(i), "a", "b", "c").expectHeight(0);
    }

    enum E1 {
        a, b, c
    }
}
