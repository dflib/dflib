package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

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
}
