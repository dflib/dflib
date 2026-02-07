package org.dflib;

import org.dflib.series.ArraySeries;
import org.dflib.series.IntArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Deprecated
public class IntSeries_ConcatTest {

    @Test
    public void none() {
        IntSeries s = new IntArraySeries(1, 2);
        assertSame(s, s.concat());
    }

    @Test
    public void self() {
        IntSeries s = new IntArraySeries(1, 2);
        Series<Integer> c = s.concat(s);
        new SeriesAsserts(c).expectData(1, 2, 1, 2);
        assertTrue(c instanceof IntSeries);
    }

    @Test
    public void intSeries() {
        IntSeries s1 = new IntArraySeries(5, 6);
        IntSeries s2 = new IntArraySeries(1, 2);
        IntSeries s3 = new IntArraySeries(3, 4);

        Series<Integer> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5, 6, 1, 2, 3, 4);
        assertTrue(c instanceof IntSeries);
    }

    @Test
    public void primitveAndNonPrimitiveSeries() {
        IntSeries s1 = new IntArraySeries(5, 6);
        Series<Integer> s2 = new ArraySeries<>(1, 2, null);
        IntSeries s3 = new IntArraySeries(3, 4);

        Series<Integer> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5, 6, 1, 2, null, 3, 4);
        assertFalse(c instanceof IntSeries);
    }
}
