package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_UnionTest {

    @Test
    public void none() {
        new SeriesAsserts(Series.union()).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void one(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        assertSame(s, Series.union(s));
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void repeated(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        Series<String> c = Series.union(s, s);
        new SeriesAsserts(c).expectData("a", "b", "a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void many(SeriesType type) {
        Series<String> s1 = type.createSeries("m", "n");
        Series<String> s2 = type.createSeries("a", "b");
        Series<String> s3 = type.createSeries("d", "c");

        Series<String> c = Series.union(s1, s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void manyIterable(SeriesType type) {
        Series<String> s1 = type.createSeries("m", "n");
        Series<String> s2 = type.createSeries("a", "b");
        Series<String> s3 = type.createSeries("d", "c");

        Series<String> c = Series.union(List.of(s1, s2, s3));
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d", "c");
    }

    @Test
    public void primitiveOptimization_int() {
        IntSeries s1 = Series.ofInt(1, 2);
        IntSeries s2 = Series.ofInt(3, 4);
        IntSeries s3 = Series.ofInt(5);

        Series<?> result = Series.union(s1, s2, s3);
        assertInstanceOf(IntSeries.class, result);
        new SeriesAsserts(result).expectData(1, 2, 3, 4, 5);
    }

    @Test
    public void primitiveOptimization_long() {
        LongSeries s1 = Series.ofLong(1L, 2L);
        LongSeries s2 = Series.ofLong(3L, 4L);
        LongSeries s3 = Series.ofLong(5L);

        Series<?> result = Series.union(s1, s2, s3);
        assertInstanceOf(LongSeries.class, result);
        new SeriesAsserts(result).expectData(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    public void primitiveOptimization_double() {
        DoubleSeries s1 = Series.ofDouble(1.1, 2.2);
        DoubleSeries s2 = Series.ofDouble(3.3, 4.4);
        DoubleSeries s3 = Series.ofDouble(5.5);

        Series<?> result = Series.union(s1, s2, s3);
        assertInstanceOf(DoubleSeries.class, result);
        new SeriesAsserts(result).expectData(1.1, 2.2, 3.3, 4.4, 5.5);
    }

    @Test
    public void primitiveOptimization_float() {
        FloatSeries s1 = Series.ofFloat(1.1f, 2.2f);
        FloatSeries s2 = Series.ofFloat(3.3f, 4.4f);
        FloatSeries s3 = Series.ofFloat(5.5f);

        Series<?> result = Series.union(s1, s2, s3);
        assertInstanceOf(FloatSeries.class, result);
        new SeriesAsserts(result).expectData(1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
    }

    @Test
    public void primitiveOptimization_bool() {
        BooleanSeries s1 = Series.ofBool(true, false);
        BooleanSeries s2 = Series.ofBool(false, true);
        BooleanSeries s3 = Series.ofBool(true);

        Series<?> result = Series.union(s1, s2, s3);
        assertInstanceOf(BooleanSeries.class, result);
        new SeriesAsserts(result).expectData(true, false, false, true, true);
    }

    @Test
    public void mixedTypes_noOptimization() {
        IntSeries s1 = Series.ofInt(1, 2);
        Series<Integer> s2 = Series.of(3, 4);
        LongSeries s3 = Series.ofLong(5L);

        Series<?> result = Series.union(s1, s2, s3);
        // Mixed types should fall back to generic Series, not a primitive series
        new SeriesAsserts(result).expectData(1, 2, 3, 4, 5L);
    }
}

