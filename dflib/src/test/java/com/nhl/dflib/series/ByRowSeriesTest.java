package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByRowSeriesTest {

    @Test
    public void testStructure() {
        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("a", "b"),
                "a1", "b1",
                "a2", "b2");

        ByRowSeries<String> s = new ByRowSeries<>(df);
        new SeriesAsserts(s).expectData("a1", "b1", "a2", "b2");
    }

    @Test
    public void testGet() {
        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("a", "b"),
                "a1", "b1",
                "a2", "b2");

        ByRowSeries<String> s = new ByRowSeries<>(df);
        assertEquals("a1", s.get(0));
        assertEquals("b1", s.get(1));
        assertEquals("a2", s.get(2));
        assertEquals("b2", s.get(3));
    }
}
