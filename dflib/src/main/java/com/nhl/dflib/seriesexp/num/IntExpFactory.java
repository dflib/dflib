package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;
import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;
import com.nhl.dflib.seriesexp.agg.DoubleExpAggregator;
import com.nhl.dflib.seriesexp.agg.IntExpAggregator;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IntExpFactory extends NumericExpFactory {

    protected static SeriesExp<Integer> cast(SeriesExp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
            return (SeriesExp<Integer>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            SeriesExp<Number> nExp = (SeriesExp<Number>) exp;
            return new IntUnarySeriesExp<>(nExp, UnarySeriesExp.toSeriesOp(Number::intValue));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new IntUnarySeriesExp<>(sExp, UnarySeriesExp.toSeriesOp(Integer::parseInt));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Integer");
    }

    @Override
    public NumericSeriesExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesExp("+",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 + n2),
                IntSeries::add);
    }

    @Override
    public NumericSeriesExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesExp("-",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 - n2),
                IntSeries::subtract);
    }

    @Override
    public NumericSeriesExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesExp("*",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 * n2),
                IntSeries::multiply);
    }

    @Override
    public NumericSeriesExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesExp("/",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 / n2),
                IntSeries::divide);
    }

    @Override
    public NumericSeriesExp<?> mod(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesExp("%",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 % n2),
                IntSeries::mod);
    }

    @Override
    public NumericSeriesExp<BigDecimal> castAsDecimal(NumericSeriesExp<?> exp, int scale) {
        return new DecimalUnarySeriesExp<>(cast(exp), UnarySeriesExp.toSeriesOp(i -> BigDecimal.valueOf((long) i).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericSeriesExp<Integer> sum(SeriesExp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.sumInt());
    }

    @Override
    public NumericSeriesExp<?> min(SeriesExp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.minInt());
    }

    @Override
    public NumericSeriesExp<?> max(SeriesExp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.maxInt());
    }

    @Override
    public NumericSeriesExp<?> avg(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.averageDouble());
    }

    @Override
    public NumericSeriesExp<?> median(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.medianDouble());
    }

    @Override
    public SeriesCondition lt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesCondition("<",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 < n2),
                IntSeries::lt);
    }

    @Override
    public SeriesCondition le(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesCondition("<=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 <= n2),
                IntSeries::le);
    }

    @Override
    public SeriesCondition gt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesCondition(">",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 > n2),
                IntSeries::gt);
    }

    @Override
    public SeriesCondition ge(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinarySeriesCondition(">=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 >= n2),
                IntSeries::ge);
    }
}
