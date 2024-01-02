package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongSeries_SelectTest {

    @Test
    public void positional() {
        Series<Long> s = Series.ofLong(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2L, 4L);
        assertTrue(s instanceof LongSeries);
    }

    @Test
    public void positional_Empty() {
        Series<Long> s = Series.ofLong(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
        assertTrue(s instanceof LongSeries);
    }

    @Test
    public void tesPositionalt_OutOfBounds() {
        LongSeries s = Series.ofLong(3, 4, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.select(0, 3).materialize());
    }

    @Test
    public void positionalNulls() {
        Series<Long> s = Series.ofLong(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2L, 4L, null);
        assertFalse(s instanceof LongSeries);
    }

    @Test
    public void booleanCondition() {
        BooleanSeries condition = Series.ofBool(false, true, true);
        Series<Long> s = Series.ofLong(3, 4, 2).select(condition);
        new SeriesAsserts(s).expectData(4L, 2L);
        assertTrue(s instanceof LongSeries);
    }
}
