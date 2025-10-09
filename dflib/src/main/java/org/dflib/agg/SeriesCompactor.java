package org.dflib.agg;

import org.dflib.Condition;
import org.dflib.DoubleSeries;
import org.dflib.Exp;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

class SeriesCompactor {

    private static final Condition notNullExp = Exp.$col(0).isNotNull();

    public static IntSeries toIntSeries(Series<? extends Number> s) {
        return (s instanceof IntSeries)
                ? (IntSeries) s
                : s.select(notNullExp).compactInt(Number::intValue);
    }

    public static LongSeries toLongSeries(Series<? extends Number> s) {
        return (s instanceof LongSeries)
                ? (LongSeries) s
                : s.select(notNullExp).compactLong(Number::longValue);
    }

    public static FloatSeries toFloatSeries(Series<? extends Number> s) {
        return (s instanceof FloatSeries)
                ? (FloatSeries) s
                : s.select(notNullExp).compactFloat(Number::floatValue);
    }

    public static DoubleSeries toDoubleSeries(Series<? extends Number> s) {
        return (s instanceof DoubleSeries)
                ? (DoubleSeries) s
                : s.select(notNullExp).compactDouble(Number::doubleValue);
    }

    public static <T> Series<T> noNullsSeries(Series<T> s) {
        return s.select(notNullExp);
    }
}
