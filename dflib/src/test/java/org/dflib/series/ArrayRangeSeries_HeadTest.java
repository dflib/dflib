package org.dflib.series;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class ArrayRangeSeries_HeadTest {

    @Test
    void test() {
        String[] s = IntStream.range(0, 33).mapToObj(String::valueOf).toArray(String[]::new);

        ArrayRangeSeries<String> r1 = new ArrayRangeSeries<>(s, 25, 8);
        new SeriesAsserts(r1.head(3)).expectData("25", "26", "27");

        ArrayRangeSeries<String> r2 = new ArrayRangeSeries<>(s, 24, 9);
        new SeriesAsserts(r2.head(3)).expectData("24", "25", "26");
    }
}
