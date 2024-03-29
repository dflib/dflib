package org.dflib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Series_PositionTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void locate(SeriesType type) {
        assertEquals(-1, type.createSeries(3, 4, 2).position(null));
        assertEquals(1, type.createSeries(3, 4, 2).position(4));
        assertEquals(-1, type.createSeries(3, 4, 2).position(5));
    }
}
