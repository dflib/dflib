package com.nhl.dflib;

import com.nhl.dflib.series.IntSequenceSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSequenceSeriesTest {

    @Test
    public void testSize() {
        assertEquals(2, new IntSequenceSeries(1, 3).size());
        assertEquals(1, new IntSequenceSeries(1, 2).size());
        assertEquals(190, new IntSequenceSeries(10, 200).size());
    }
}
