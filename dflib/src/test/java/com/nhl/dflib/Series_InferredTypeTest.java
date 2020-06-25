package com.nhl.dflib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_InferredTypeTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testSameType(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        assertSame(String.class, s.getInferredType());
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testSubclassSuperclassTypes(SeriesType type) {
        Series<Object> s = type.createSeries(
                new java.sql.Date(System.currentTimeMillis()),
                new java.util.Date(System.currentTimeMillis()));
        assertSame(java.util.Date.class, s.getInferredType());
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testCommonAncestorTypes(SeriesType type) {
        Series<Object> s = type.createSeries(Long.valueOf(5), Integer.valueOf(6));
        assertSame(Number.class, s.getInferredType());
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testNoCommonAncestorTypes(SeriesType type) {
        Series<Object> s = type.createSeries(Long.valueOf(5), "YYY");
        assertSame(Object.class, s.getInferredType());
    }
}

