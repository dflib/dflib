package com.nhl.dflib;

import com.nhl.dflib.unit.IndexAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IndexTest {

    @Test
    public void testPositions() {
        Index i = Index.forLabels("a", "b", "c", "d");

        assertArrayEquals(new int[0], i.positions());
        assertArrayEquals(new int[]{0, 2}, i.positions("a", "c"));
        assertArrayEquals(new int[]{2, 0}, i.positions("c", "a"));
    }

    @Test
    public void testPositions_NoLabel() {
        Index i = Index.forLabels("a", "b");
        assertThrows(IllegalArgumentException.class, () -> i.positions("a", "c"));
    }

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

    @Test
    public void testRangeOpenClosed_OutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> Index.forLabels("a", "b", "c", "d").rangeOpenClosed(0, 5));
    }

    @Test
    public void testToSeries() {
        Series<String> s = Index.forLabels("a", "b", "c", "d").toSeries();
        new SeriesAsserts(s).expectData("a", "b", "c", "d");
    }

    enum E1 {
        a, b, c
    }
}
