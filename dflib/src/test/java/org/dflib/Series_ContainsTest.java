package org.dflib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class Series_ContainsTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void contains(SeriesType type) {
        assertFalse(type.createSeries(3, 4, 2).contains(null));
        assertTrue(type.createSeries(3, 4, 2).contains(4));
        assertFalse(type.createSeries(3, 4, 2).contains(5));
    }
}
