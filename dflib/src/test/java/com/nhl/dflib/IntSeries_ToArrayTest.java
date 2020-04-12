package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntSeries_ToArrayTest {

    @Test
    public void testContents() {
        int[] a = new IntArraySeries(1, 2).toIntArray();
        assertArrayEquals(new int[]{1, 2}, a);
    }

    @Test
    public void testMutability() {
        IntSeries s = new IntArraySeries(1, 2);
        int[] a  = s.toIntArray();
        a[0] = -1;

        new IntSeriesAsserts(s).expectData(1, 2);
    }
}
