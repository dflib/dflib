package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Series_ReduceTest {

    @Test
    public void doubleSeries() {
        DoubleSeries doubleSeries = Series.ofDouble(1, 2, 3, 4, 5);
        assertEquals(doubleSeries.sum(), doubleSeries.reduce(0d, (a, b) -> a + b));
    }

    @Test
    public void intSeries() {
        IntSeries intSeries = Series.ofInt(1, 2, 3, 4, 5);
        assertEquals(15, intSeries.reduce(0, (a, b) -> a + b));
    }

    @Test
    public void longSeries() {
        LongSeries longSeries = Series.ofLong(1, 2, 3, 4, 5);
        assertEquals(15L, longSeries.reduce(0L, (a, b) -> a + b));
    }

    @Test
    public void booleanSeries() {
        assertEquals(false, Series.of(false, false).reduce(false, (a, b) -> a || b));
        assertEquals(true, Series.of(false, false, true).reduce(false, (a, b) -> a || b));
    }

}
