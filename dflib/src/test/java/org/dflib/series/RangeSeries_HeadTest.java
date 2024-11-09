package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class RangeSeries_HeadTest {

    @Test
    void test() {
        String[] vals = IntStream.range(0, 33).mapToObj(String::valueOf).toArray(String[]::new);
        Series<String> s = Series.of(vals);

        Series<String> r1 = new RangeSeries<>(s, 25, 8);
        new SeriesAsserts(r1.head(3)).expectData("25", "26", "27");

        Series<String> r2 = new RangeSeries<>(s, 24, 9);
        new SeriesAsserts(r2.head(3)).expectData("24", "25", "26");
    }
}
