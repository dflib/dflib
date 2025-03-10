package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_SetTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    void set(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c");
        assertSame(s, s.replace(1, "b"));

        new SeriesAsserts(s.replace(0, "A")).expectData("A", "b", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    void set_Nulls(SeriesType type) {
        Series<String> s = type.createSeries("a", null, "c");
        assertSame(s, s.replace(1, null));

        new SeriesAsserts(s.replace(0, null).replace(1, "B")).expectData(null, "B", "c");
    }
}
