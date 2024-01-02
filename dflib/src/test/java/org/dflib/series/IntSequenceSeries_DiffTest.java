package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class IntSequenceSeries_DiffTest {

    @Test
    public void withEmpty() {
        IntSequenceSeries s = new IntSequenceSeries(1, 4);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        IntSequenceSeries s = new IntSequenceSeries(1, 4);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void withNull() {
        IntSequenceSeries s = new IntSequenceSeries(1, 4);
        new SeriesAsserts(s.diff(Series.of(null, null))).expectData(1, 2, 3);
    }

    @Test
    public void withSequence() {
        IntSequenceSeries s = new IntSequenceSeries(1, 4);

        IntSequenceSeries s1 = new IntSequenceSeries(2, 5);
        new SeriesAsserts(s.diff(s1)).expectData(1);

        IntSequenceSeries s2 = new IntSequenceSeries(2, 3);
        new SeriesAsserts(s.diff(s2)).expectData(1, 3);

        IntSequenceSeries s3 = new IntSequenceSeries(0, 2);
        new SeriesAsserts(s.diff(s3)).expectData(2, 3);

        IntSequenceSeries s4 = new IntSequenceSeries(0, 5);
        new SeriesAsserts(s.diff(s4)).expectData();
    }

    @Test
    public void diff() {
        IntSequenceSeries s = new IntSequenceSeries(1, 4);

        Series<Integer> s1 = Series.of(null, 2, 3, 4);
        new SeriesAsserts(s.diff(s1)).expectData(1);

        Series<Integer> s2 = Series.of(4, -1);
        new SeriesAsserts(s.diff(s2)).expectData(1, 2, 3);
    }
}
