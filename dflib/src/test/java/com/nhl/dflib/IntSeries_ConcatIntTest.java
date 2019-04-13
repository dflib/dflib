package com.nhl.dflib;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntSeries_ConcatIntTest {

    @Test
    public void testConcatInt_None() {
        IntSeries s = new IntArraySeries(1, 2);
        assertSame(s, s.concatInt());
    }

    @Test
    public void testConcatInt_Self() {
        IntSeries s = new IntArraySeries(1, 2);
        IntSeries c = s.concatInt(s);
        new IntSeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void testConcatInt() {
        IntSeries s1 = new IntArraySeries(34, 23);
        IntSeries s2 = new IntArraySeries(1, 2);
        IntSeries s3 = new IntArraySeries(-1, -6);

        IntSeries c = s1.concatInt(s2, s3);
        new IntSeriesAsserts(c).expectData(34, 23, 1, 2, -1, -6);
    }
}
