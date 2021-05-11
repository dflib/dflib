package com.nhl.dflib.exp.num;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;
import com.nhl.dflib.exp.UnarySeriesExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.agg.LongExpAggregator;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LongExpFactory extends NumericExpFactory {

    protected static SeriesExp<Long> cast(SeriesExp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Long.class)) {
            return (SeriesExp<Long>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            SeriesExp<Number> nExp = (SeriesExp<Number>) exp;
            return new LongUnaryExp<>(nExp, UnarySeriesExp.toSeriesOp(Number::longValue));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new LongUnaryExp<>(sExp, UnarySeriesExp.toSeriesOp(Long::parseLong));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Long");
    }

    @Override
    public NumericExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinaryExp("+",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Long n1, Long n2) -> n1 + n2),
                LongSeries::add);
    }

    @Override
    public NumericExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinaryExp("-",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Long n1, Long n2) -> n1 - n2),
                LongSeries::subtract);
    }

    @Override
    public NumericExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinaryExp("*",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Long n1, Long n2) -> n1 * n2),
                LongSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinaryExp("/",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Long n1, Long n2) -> n1 / n2),
                LongSeries::divide);
    }

    @Override
    public NumericExp<?> mod(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinaryExp("%",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Long n1, Long n2) -> n1 % n2),
                LongSeries::mod);
    }


    @Override
    public NumericExp<BigDecimal> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnarySeriesExp.toSeriesOp(l -> BigDecimal.valueOf(l).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericExp<Long> sum(SeriesExp<? extends Number> exp) {
        return new LongExpAggregator<>(exp, AggregatorFunctions.sumLong());
    }

    @Override
    public NumericExp<?> min(SeriesExp<? extends Number> exp) {
        return new LongExpAggregator<>(exp, AggregatorFunctions.minLong());
    }

    @Override
    public NumericExp<?> max(SeriesExp<? extends Number> exp) {
        return new LongExpAggregator<>(exp, AggregatorFunctions.maxLong());
    }

    @Override
    public NumericExp<?> avg(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.averageDouble());
    }

    @Override
    public NumericExp<?> median(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.medianDouble());
    }

    @Override
    public SeriesCondition lt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinarySeriesCondition("<",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Long n1, Long n2) -> n1 < n2),
                LongSeries::lt);
    }

    @Override
    public SeriesCondition le(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinarySeriesCondition("<=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Long n1, Long n2) -> n1 <= n2),
                LongSeries::le);
    }

    @Override
    public SeriesCondition gt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinarySeriesCondition(">",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Long n1, Long n2) -> n1 > n2),
                LongSeries::gt);
    }

    @Override
    public SeriesCondition ge(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new LongBinarySeriesCondition(">=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Long n1, Long n2) -> n1 >= n2),
                LongSeries::ge);
    }
}
