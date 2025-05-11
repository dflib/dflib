package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class IntReverseSequenceSeries_DiffTest {

    @Test
    public void withEmpty() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void withNull() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);
        new SeriesAsserts(s.diff(Series.of(null, null))).expectData(4, 3, 2);
    }

    @Test
    public void withSequence() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);

        IntReverseSequenceSeries s1 = new IntReverseSequenceSeries(5, 2);
        new SeriesAsserts(s.diff(s1)).expectData(2);

        IntReverseSequenceSeries s2 = new IntReverseSequenceSeries(3, 2);
        new SeriesAsserts(s.diff(s2)).expectData(4, 2);

        IntReverseSequenceSeries s3 = new IntReverseSequenceSeries(2, 0);
        new SeriesAsserts(s.diff(s3)).expectData(4, 3);

        IntReverseSequenceSeries s4 = new IntReverseSequenceSeries(5, 0);
        new SeriesAsserts(s.diff(s4)).expectData();
    }

    @Test
    public void diff() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);

        Series<Integer> s1 = Series.of(null, 3, 2, 1);
        new SeriesAsserts(s.diff(s1)).expectData(4);

        Series<Integer> s2 = Series.of(4, -1);
        new SeriesAsserts(s.diff(s2)).expectData(3, 2);
    }
}
