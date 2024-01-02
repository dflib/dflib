package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Series_SelectTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positional(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2, 4);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positional_Empty(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positional_OutOfBounds(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select(0, 3);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.materialize());
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positionalNulls(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2, 4, null);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void booleanCondition(SeriesType type) {
        BooleanSeries condition = Series.ofBool(false, true, true);
        Series<Integer> s = type.createSeries(3, 4, 2).select(condition);
        new SeriesAsserts(s).expectData(4, 2);
    }
}
