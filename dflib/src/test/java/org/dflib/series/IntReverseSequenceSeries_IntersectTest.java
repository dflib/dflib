package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntReverseSequenceSeries_IntersectTest {

    @Test
    public void withEmpty() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);
        new SeriesAsserts(s.intersect(s)).expectData(4, 3, 2);
    }

    @Test
    public void withNull() {
        IntSeries s = new IntReverseSequenceSeries(4, 1);
        new SeriesAsserts(s.intersect(Series.of(null, null))).expectData();
    }

    @Test
    public void withSequence() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);

        IntReverseSequenceSeries s1 = new IntReverseSequenceSeries(5, 2);
        new SeriesAsserts(s.intersect(s1)).expectData(4, 3);

        IntReverseSequenceSeries s2 = new IntReverseSequenceSeries(3, 2);
        new SeriesAsserts(s.intersect(s2)).expectData(3);

        IntReverseSequenceSeries s3 = new IntReverseSequenceSeries(2, 0);
        new SeriesAsserts(s.intersect(s3)).expectData(2);

        IntReverseSequenceSeries s4 = new IntReverseSequenceSeries(5, 0);
        new SeriesAsserts(s.intersect(s4)).expectData(4, 3, 2);
    }

    @Test
    public void intersect() {
        IntReverseSequenceSeries s = new IntReverseSequenceSeries(4, 1);

        Series<Integer> s1 = Series.of(null, 1, 2, 3);
        new SeriesAsserts(s.intersect(s1)).expectData(3, 2);

        Series<Integer> s2 = Series.of(-1, 5);
        new SeriesAsserts(s.intersect(s2)).expectData();
    }
}
