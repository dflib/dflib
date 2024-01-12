package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntArraySeriesTest {

    @Test
    public void getInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(1, s.getInt(0));
        assertEquals(2, s.getInt(1));
    }

    @Test
    public void copyTo() {

        IntArraySeries s = new IntArraySeries(1, 2, 3, 4);

        Object[] b1 = new Object[5];
        s.copyTo(b1, 0, 1, 4);
        assertArrayEquals(new Object[]{null, 1, 2, 3, 4}, b1);

        Object[] b2 = new Object[5];
        s.copyTo(b2, 1, 1, 3);
        assertArrayEquals(new Object[]{null, 2, 3, 4, null}, b2);

        Object[] b3 = new Object[5];
        s.copyTo(b3, 3, 1, 1);
        assertArrayEquals(new Object[]{null, 4, null, null, null}, b3);
    }
}
