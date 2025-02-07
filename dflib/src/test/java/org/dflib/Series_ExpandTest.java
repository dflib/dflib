package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class Series_ExpandTest {

    @Test
    public void expand() {
        Series<?> s = Series.of(3, "A").expand("abc", 4);
        new SeriesAsserts(s).expectData(3, "A", "abc", 4);
    }
}
