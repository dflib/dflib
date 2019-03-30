package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class IndexTest {

    @Test
    public void testWithNames_Enum() {
        Index i = Index.forLabels(E1.class);

        // TODO: a test helper for Index
        new DFAsserts(DataFrame.forRows(i), "a", "b", "c").expectHeight(0);
    }

    enum E1 {
        a, b, c
    }
}
