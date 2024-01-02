package org.dflib.series;

import org.dflib.DataFrame;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ByRowSeriesTest {

    @Test
    public void structure() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "a1", "b1",
                "a2", "b2");

        ByRowSeries s = new ByRowSeries(df);
        new SeriesAsserts(s).expectData("a1", "b1", "a2", "b2");
    }

    @Test
    public void get() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "a1", "b1",
                "a2", "b2");

        ByRowSeries s = new ByRowSeries(df);
        assertEquals("a1", s.get(0));
        assertEquals("b1", s.get(1));
        assertEquals("a2", s.get(2));
        assertEquals("b2", s.get(3));
    }
}
