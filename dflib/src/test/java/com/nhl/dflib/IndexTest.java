package com.nhl.dflib;

import com.nhl.dflib.unit.IndexAsserts;
import org.junit.Test;

public class IndexTest {

    @Test
    public void testWithNames_Enum() {
        Index i = Index.forLabels(E1.class);
        IndexAsserts.expect(i, "a", "b", "c");
    }

    @Test
    public void testRangeOpenClosed0() {
        Index i = Index.forLabels("a", "b", "c", "d").rangeOpenClosed(1, 3);
        IndexAsserts.expect(i, "b", "c");
    }

    @Test
    public void testRangeOpenClosed1() {
        Index i = Index.forLabels("a", "b", "c", "d").rangeOpenClosed(0, 4);
        IndexAsserts.expect(i, "a", "b", "c", "d");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeOpenClosed_OutOfRange() {
        Index i = Index.forLabels("a", "b", "c", "d").rangeOpenClosed(0, 5);
    }

    enum E1 {
        a, b, c
    }
}
