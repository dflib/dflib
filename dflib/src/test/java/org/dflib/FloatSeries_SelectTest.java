package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FloatSeries_SelectTest {

    @Test
    public void test() {
        Series<Float> s = Series.ofFloat(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2f, 4f);
        assertInstanceOf(FloatSeries.class, s);
    }

    @Test
    public void empty() {
        Series<Float> s = Series.ofFloat(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
        assertInstanceOf(FloatSeries.class, s);
    }

    @Test
    public void outOfBounds() {
        FloatSeries s =  Series.ofFloat(3, 4, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.select(0, 3).materialize());
    }

    @Test
    public void nulls() {
        Series<Float> s = Series.ofFloat(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2f, 4f, null);
    }

    @Test
    public void booleanCondition() {
        BooleanSeries condition = Series.ofBool(false, true, true);
        Series<Float> s = Series.ofFloat(3, 4, 2).select(condition);
        new SeriesAsserts(s).expectData(4f, 2f);
        assertInstanceOf(FloatSeries.class, s);
    }
}
