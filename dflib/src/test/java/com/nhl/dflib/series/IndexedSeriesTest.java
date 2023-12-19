package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndexedSeriesTest {

    @Test
    public void iterateCausesMaterialization() {
        IndexedSeries<String> s = new IndexedSeries<>(
                Series.of("a", "b", "c"),
                Series.ofInt(0, 2)
        );

        assertFalse(s.isMaterialized());

        // force iterator
        for (String e : s) {
            e.length();
        }
        
        assertTrue(s.isMaterialized());

        new SeriesAsserts(s).expectData("a", "c");
    }
}
