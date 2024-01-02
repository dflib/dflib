package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSequenceSeries_IntersectTest {

    @Test
    public void withEmpty() {
        IntSeries s = new IntSequenceSeries(1, 4);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        IntSeries s = new IntSequenceSeries(1, 4);
        new SeriesAsserts(s.intersect(s)).expectData(1, 2, 3);
    }

    @Test
    public void withNull() {
        IntSeries s = new IntSequenceSeries(1, 4);
        new SeriesAsserts(s.intersect(Series.of(null, null))).expectData();
    }

    @Test
    public void withSequence() {
        IntSequenceSeries s = new IntSequenceSeries(1, 4);

        IntSequenceSeries s1 = new IntSequenceSeries(2, 5);
        new SeriesAsserts(s.intersect(s1)).expectData(2, 3);

        IntSequenceSeries s2 = new IntSequenceSeries(2, 3);
        new SeriesAsserts(s.intersect(s2)).expectData(2);

        IntSequenceSeries s3 = new IntSequenceSeries(0, 2);
        new SeriesAsserts(s.intersect(s3)).expectData(1);

        IntSequenceSeries s4 = new IntSequenceSeries(0, 5);
        new SeriesAsserts(s.intersect(s4)).expectData(1, 2, 3);
    }

    @Test
    public void intersect() {
        IntSeries s = new IntSequenceSeries(1, 4);

        Series<Integer> s1 = Series.of(null, 2, 3, 4);
        new SeriesAsserts(s.intersect(s1)).expectData(2, 3);

        Series<Integer> s2 = Series.of(4, -1);
        new SeriesAsserts(s.intersect(s2)).expectData();
    }
}
