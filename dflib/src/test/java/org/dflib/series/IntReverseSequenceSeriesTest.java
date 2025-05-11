package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntReverseSequenceSeriesTest {

    @Test
    public void size() {
        IntReverseSequenceSeries s1 = new IntReverseSequenceSeries(5, -1);
        assertEquals(6, s1.size());

        IntReverseSequenceSeries s2 = new IntReverseSequenceSeries(5, 1);
        assertEquals(4, s2.size());

        IntReverseSequenceSeries s3 = new IntReverseSequenceSeries(1, 0);
        assertEquals(1, s3.size());
    }

    @Test
    public void sizeZero() {
        IntReverseSequenceSeries s1 = new IntReverseSequenceSeries(0, 0);
        assertEquals(0, s1.size());
    }

    @Test
    public void getInt() {
        IntReverseSequenceSeries s1 = new IntReverseSequenceSeries(5, -1);
        assertEquals(5, s1.getInt(0));
        assertEquals(4, s1.getInt(1));
        assertEquals(3, s1.getInt(2));
        assertEquals(2, s1.getInt(3));
        assertEquals(1, s1.getInt(4));
        assertEquals(0, s1.getInt(5));
    }

    @Test
    public void copyToInt() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(2, -2);

        int[] c1 = new int[3];
        s.copyToInt(c1, 1, 0, 3);
        assertArrayEquals(new int[] {1, 0, -1}, c1);

        int[] c2 = new int[4];
        s.copyToInt(c2, 0, 0, 4);
        assertArrayEquals(new int[] {2, 1, 0, -1}, c2);
    }
}
