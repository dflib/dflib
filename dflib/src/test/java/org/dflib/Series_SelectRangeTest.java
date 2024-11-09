package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.stream.IntStream;

public class Series_SelectRangeTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void _5(SeriesType type) {
        Series<String> range = type.createSeries("a", "b", "c", "d", "e").selectRange(2, 5);
        new SeriesAsserts(range).expectData("c", "d", "e");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void _32(SeriesType type) {

        String[] vals = IntStream.range(0, 33).mapToObj(String::valueOf).toArray(String[]::new);
        Series<String> s = type.createSeries(vals);

        Series<String> r1 = s.selectRange(25, 33);
        new SeriesAsserts(r1).expectData("25", "26", "27", "28", "29", "30", "31", "32");

        Series<String> r2 = s.selectRange(24, 33);
        new SeriesAsserts(r2).expectData("24", "25", "26", "27", "28", "29", "30", "31", "32");
    }
}
