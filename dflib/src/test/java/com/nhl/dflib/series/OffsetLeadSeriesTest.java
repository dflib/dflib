package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OffsetLeadSeriesTest {

    @Test
    public void get() {

        Series<String> s = new OffsetLeadSeries<>(Series.of("a", "b", "c", "d"), 1, "X");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.get(-1));
        assertEquals("X", s.get(0));
        assertEquals("a", s.get(1));
        assertEquals("b", s.get(2));
        assertEquals("c", s.get(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.get(4));
    }

    @Test
    public void materialize() {
        Series<String> s = new OffsetLeadSeries<>(Series.of("a", "b", "c", "d"), 1, "X").materialize();
        assertTrue(s instanceof ArraySeries);
        new SeriesAsserts(s).expectData("X", "a", "b", "c");
    }

    @Test
    public void copyTo() {

        Series<String> s = new OffsetLeadSeries<>(Series.of("a", "b", "c", "d"), 1, "X");

        Object[] b1 = new Object[5];
        s.copyTo(b1, 0, 1, 4);
        assertArrayEquals(new Object[]{null, "X", "a", "b", "c"}, b1);

        Object[] b2 = new Object[5];
        s.copyTo(b2, 1, 1, 3);
        assertArrayEquals(new Object[]{null, "a", "b", "c", null}, b2);

        Object[] b3 = new Object[5];
        s.copyTo(b3, 3, 1, 1);
        assertArrayEquals(new Object[]{null, "c", null, null, null}, b3);

        Object[] b4 = new Object[5];
        s.copyTo(b4, 0, 1, 1);
        assertArrayEquals(new Object[]{null, "X", null, null, null}, b4);
    }

    @Test
    public void shift() {

        Series<String> s = new OffsetLeadSeries<>(Series.of("a", "b", "c", "d"), 1, "X");

        new SeriesAsserts(s.shift(-1)).expectData("a", "b", "c", null);
        new SeriesAsserts(s.shift(-1, "X")).expectData("a", "b", "c", "X");
        new SeriesAsserts(s.shift(-1, "Y")).expectData("a", "b", "c", "Y");
        new SeriesAsserts(s.shift(1, "X")).expectData("X", "X", "a", "b");
        new SeriesAsserts(s.shift(1, "Y")).expectData("Y", "X", "a", "b");
        new SeriesAsserts(s.shift(0, "Y")).expectData("X", "a", "b", "c");
    }
}
