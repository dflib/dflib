package org.dflib.builder;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class ObjectAccumTest {

    @Test
    public void fill_Series_SmallSize() {
        ObjectAccum<Integer> accum = new ObjectAccum<>(10);
        accum.fill(Series.of(1, 2), 0, 0, 2);
        new SeriesAsserts(accum.toSeries()).expectData(1, 2);
    }

    @Test
    public void fill_Series_Resize() {
        ObjectAccum<Integer> accum = new ObjectAccum<>(2);
        accum.fill(Series.of(1, 2, 3, 4), 0, 2, 4);
        new SeriesAsserts(accum.toSeries()).expectData(null, null, 1, 2, 3, 4);
    }


}
