package org.dflib;

import org.dflib.series.IntArraySeries;
import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntSeries_ToArrayTest {

    @Test
    public void contents() {
        int[] a = new IntArraySeries(1, 2).toIntArray();
        assertArrayEquals(new int[]{1, 2}, a);
    }

    @Test
    public void mutability() {
        IntSeries s = new IntArraySeries(1, 2);
        int[] a  = s.toIntArray();
        a[0] = -1;

        new IntSeriesAsserts(s).expectData(1, 2);
    }
}
