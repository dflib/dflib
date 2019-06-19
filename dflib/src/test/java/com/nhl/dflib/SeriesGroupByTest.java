package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class SeriesGroupByTest {

    @Test
    public void testToSeries() {
        SeriesGroupBy<String> gb = Series.forData("a", "b", "cd", "e", "fg")
                .group((String s) -> s.length());

        new SeriesAsserts(gb.toSeries()).expectData("a", "b", "e", "cd", "fg");
    }
}
