package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.DoubleSeriesAsserts;
import org.dflib.unit.FloatSeriesAsserts;
import org.dflib.unit.IntSeriesAsserts;
import org.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_CompactTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactBool(SeriesType type) {
        BooleanSeries s = type.createSeries(true, "FALSE", "TRUE", 0, 1, null).compactBool();
        new BoolSeriesAsserts(s).expectData(true, false, true, false, true, false);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactBool_Mapper(SeriesType type) {
        BooleanSeries s = type.createSeries(true, "FALSE", "TRUE", 0, 1, null).compactBool(v -> !BoolValueMapper.of().map(v));
        new BoolSeriesAsserts(s).expectData(false, true, false, true, false, true);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactInt(SeriesType type) {
        IntSeries s = type.createSeries(1, "55", "-15", null).compactInt(-1);
        new IntSeriesAsserts(s).expectData(1, 55, -15, -1);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactInt_Mapper(SeriesType type) {
        IntSeries s = type.createSeries(1, "55", "-15", null).compactInt(v -> IntValueMapper.of(-1).map(v) * 2);
        new IntSeriesAsserts(s).expectData(2, 110, -30, -2);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactLong(SeriesType type) {
        LongSeries s = type.createSeries(1, 5L, "55", null).compactLong(-1L);
        new LongSeriesAsserts(s).expectData(1L, 5L, 55L, -1L);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactLong_Mapper(SeriesType type) {
        LongSeries s = type.createSeries(1, 5L, "55", null).compactLong(v -> LongValueMapper.of(-1).map(v) * 2);
        new LongSeriesAsserts(s).expectData(2L, 10L, 110L, -2L);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactFloat(SeriesType type) {
        FloatSeries s = type.createSeries(1.1f, 5.01d, 8, "55.2", null).compactFloat(-1.2f);
        new FloatSeriesAsserts(s).expectData(1.1f, 5.01f, 8f, 55.2f, -1.2f);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactFloat_Mapper(SeriesType type) {
        FloatSeries s = type.createSeries(1.1f, 5.01d, 8, "55.2", null).compactFloat(v -> FloatValueMapper.of(-1.2f).map(v) * 2);
        new FloatSeriesAsserts(s).expectData(2.2f, 10.02f, 16f, 110.4f, -2.4f);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactDouble(SeriesType type) {
        DoubleSeries s = type.createSeries(1.1d, 5.1f, 8, "55.2", null).compactDouble(-1.2d);
        new DoubleSeriesAsserts(s).delta(0.00001).expectData(1.1d, 5.1d, 8d, 55.2d, -1.2d);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void compactDouble_Mapper(SeriesType type) {
        DoubleSeries s = type.createSeries(1.1d, 5.1f, 8, "55.2", null).compactDouble(v -> DoubleValueMapper.of(-1.2d).map(v) * 2);
        new DoubleSeriesAsserts(s).delta(0.00001).expectData(2.2d, 10.2d, 16d, 110.4d, -2.4d);
    }
}
