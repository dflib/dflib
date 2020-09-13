package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OffsetLagSeriesTest {

    @Test
    public void testGet() {

        Series<String> s = new OffsetLagSeries<>(Series.forData("a", "b", "c", "d"), -1, "X");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.get(-1));
        assertEquals("b", s.get(0));
        assertEquals("c", s.get(1));
        assertEquals("d", s.get(2));
        assertEquals("X", s.get(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.get(4));
    }

    @Test
    public void testMaterialize() {
        Series<String> s = new OffsetLagSeries<>(Series.forData("a", "b", "c", "d"), -1, "X").materialize();
        assertTrue(s instanceof ArraySeries);
        new SeriesAsserts(s).expectData("b", "c", "d", "X");
    }

    @Test
    public void testCopyTo() {

        Series<String> s = new OffsetLagSeries<>(Series.forData("a", "b", "c", "d"), -1, "X");

        Object[] b1 = new Object[5];
        s.copyTo(b1, 0, 1, 4);
        assertArrayEquals(new Object[]{null, "b", "c", "d", "X"}, b1);

        Object[] b2 = new Object[5];
        s.copyTo(b2, 1, 1, 3);
        assertArrayEquals(new Object[]{null, "c", "d", "X", null}, b2);

        Object[] b3 = new Object[5];
        s.copyTo(b3, 3, 1, 1);
        assertArrayEquals(new Object[]{null, "X", null, null, null}, b3);
    }

    @Test
    public void testShift() {

        Series<String> s = new OffsetLagSeries<>(Series.forData("a", "b", "c", "d"), -1, "X");

        new SeriesAsserts(s.shift(-1)).expectData("c", "d", "X", null);
        new SeriesAsserts(s.shift(-1, "X")).expectData("c", "d", "X", "X");
        new SeriesAsserts(s.shift(-1, "Y")).expectData("c", "d", "X", "Y");
        new SeriesAsserts(s.shift(1, "Y")).expectData("Y", "b", "c", "d");
        new SeriesAsserts(s.shift(0, "Y")).expectData("b", "c", "d", "X");
    }
}
