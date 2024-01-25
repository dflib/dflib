package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntSequenceSeries_RangeTest {

    @Test
    public void range() {
        IntSeries s = new IntSequenceSeries(1, 5);
        new IntSeriesAsserts(s.rangeInt(1, 4)).expectData(2, 3, 4);
        new IntSeriesAsserts(s.rangeInt(0, 2)).expectData(1, 2);
        new IntSeriesAsserts(s.rangeInt(2, 4)).expectData(3, 4);
    }

    @Test
    public void empty() {
        IntSeries s = new IntSequenceSeries(1, 4).rangeInt(0, 0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void self() {
        IntSeries s = new IntSequenceSeries(1, 4);
        IntSeries s1 = s.rangeInt(0, 3);
        assertSame(s, s1);
    }

    @Test
    public void outOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> new IntSequenceSeries(1, 4).rangeInt(-1, 10));
    }
}
