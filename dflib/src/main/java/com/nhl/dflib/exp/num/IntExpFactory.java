package com.nhl.dflib.exp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;
import com.nhl.dflib.exp.UnarySeriesExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.agg.IntExpAggregator;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

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
            return new IntUnaryExp<>(nExp, UnarySeriesExp.toSeriesOp(Number::intValue));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new IntUnaryExp<>(sExp, UnarySeriesExp.toSeriesOp(Integer::parseInt));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Integer");
    }

    @Override
    public NumericExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinaryExp("+",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 + n2),
                IntSeries::add);
    }

    @Override
    public NumericExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinaryExp("-",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 - n2),
                IntSeries::subtract);
    }

    @Override
    public NumericExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinaryExp("*",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 * n2),
                IntSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinaryExp("/",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 / n2),
                IntSeries::divide);
    }

    @Override
    public NumericExp<?> mod(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new IntBinaryExp("%",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Integer n1, Integer n2) -> n1 % n2),
                IntSeries::mod);
    }

    @Override
    public NumericExp<BigDecimal> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnarySeriesExp.toSeriesOp(i -> BigDecimal.valueOf((long) i).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericExp<Integer> sum(SeriesExp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.sumInt());
    }

    @Override
    public NumericExp<?> min(SeriesExp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.minInt());
    }

    @Override
    public NumericExp<?> max(SeriesExp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.maxInt());
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
