package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class LazyIndexedSeriesTest {

    @Test
    public void iterateWithoutMaterialization() {
        LazyIndexedSeries<String> s = new LazyIndexedSeries<>(
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
}
