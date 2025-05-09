package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSequenceSeriesTest {

    @Test
    public void size() {
        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(6, s1.size());

        IntSequenceSeries s2 = new IntSequenceSeries(1, 5);
        assertEquals(4, s2.size());

        IntSequenceSeries s3 = new IntSequenceSeries(0, 1);
        assertEquals(1, s3.size());
    }

    @Test
    public void sizeZero() {
        IntSequenceSeries s1 = new IntSequenceSeries(0, 0);
        assertEquals(0, s1.size());
    }

    @Test
    public void getInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 5);
        assertEquals(1, s.getInt(0));
        assertEquals(2, s.getInt(1));
        assertEquals(3, s.getInt(2));
        assertEquals(4, s.getInt(3));
    }

    @Test
    public void copyToInt() {
        IntSequenceSeries s = new IntSequenceSeries(-2, 2);

        int[] c1 = new int[3];
        s.copyToInt(c1, 1, 0, 3);
        assertArrayEquals(new int[] {-1, 0, 1}, c1);

        int[] c2 = new int[4];
        s.copyToInt(c2, 0, 0, 4);
        assertArrayEquals(new int[] {-2, -1, 0, 1}, c2);
    }
}
