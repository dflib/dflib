package com.nhl.dflib.series;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntSeriesTest {

    @Test
    public void testConcat_None() {
        IntSeries s = new IntSeries(1, 2);
        assertSame(s, s.concat());
    }

    @Test
    public void testConcat_Self() {
        IntSeries s = new IntSeries(1, 2);
        IntSeries c = s.concat(s);
        new IntSeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void testConcat() {
        IntSeries s1 = new IntSeries(34, 23);
        IntSeries s2 = new IntSeries(1, 2);
        IntSeries s3 = new IntSeries(-1, -6);


        IntSeries c = s1.concat(s2, s3);
        new IntSeriesAsserts(c).expectData(34, 23, 1, 2, -1, -6);
    }
}
