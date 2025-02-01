package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayRangeSeriesTest {

    @Test
    void nominalType() {
        String[] s = IntStream.range(0, 33).mapToObj(String::valueOf).toArray(String[]::new);
        ArrayRangeSeries<String> r1 = new ArrayRangeSeries<>(s, 25, 8);

        assertEquals(String.class, r1.getNominalType());
    }

    @Test
    void materialize() {
        Integer[] s = IntStream.range(0, 33).mapToObj(Integer::valueOf).toArray(Integer[]::new);
        Series<Integer> r1 = new ArrayRangeSeries<>(s, 25, 8).materialize();

        new SeriesAsserts(r1).expectData(25, 26, 27, 28, 29, 30, 31, 32);
        assertEquals(Integer.class, r1.getNominalType());
    }
}
