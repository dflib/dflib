package org.dflib.series;

import org.dflib.series.IntSequenceSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
