package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class IndexedSeriesTest {

    @Test
    public void iterateWithoutMaterialization() {
        IndexedSeries<String> s = new IndexedSeries<>(
                Series.of("a", "b", "c"),
                Series.ofInt(0, 2)
        );

        // force iterator
        for (String e : s) {
            e.length();
        }

        assertFalse(s.isMaterialized());

        new SeriesAsserts(s).expectData("a", "c");
    }

    @Test
    public void compact() {
        Series<String> s1 = Series.of(
                new String("A"),
                new String("a"),
                new String("B"),
                new String("a"),
                new String("A"),
                new String("A"));

        IndexedSeries<String> s2 = new IndexedSeries<>(s1, Series.ofInt(0, 1, 2, 4));

        Series<String> s3 = s2.compact();
        new SeriesAsserts(s3).expectData("A", "a", "B", "A");
        assertEquals(3, s3.map(System::identityHashCode).unique().size());

        Series<String> s4 = s2.compact();
        new SeriesAsserts(s4).expectData("A", "a", "B", "A");
        assertEquals(3, s4.map(System::identityHashCode).unique().size());
    }

    @Test
    public void materialize_compact() {
        Series<String> s1 = Series.of(
                new String("A"),
                new String("a"),
                new String("B"),
                new String("a"),
                new String("A"),
                new String("A"));

        IndexedSeries<String> s2 = new IndexedSeries<>(s1, Series.ofInt(0, 1, 2, 4));

        // check that internal state is valid after materialization and compaction
        s2.materialize();
        Series<String> s3 = s2.compact();
        
        new SeriesAsserts(s3).expectData("A", "a", "B", "A");
        assertEquals(3, s3.map(System::identityHashCode).unique().size());
    }
}
