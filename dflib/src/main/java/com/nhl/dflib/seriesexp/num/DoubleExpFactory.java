package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.seriesexp.BinarySeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleExpFactory extends NumericExpFactory {

    protected static SeriesExp<Double> cast(SeriesExp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Double.class)) {
            return (SeriesExp<Double>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            SeriesExp<Number> nExp = (SeriesExp<Number>) exp;
            return new DoubleUnarySeriesExp<>(nExp, UnarySeriesExp.toSeriesOp(Number::doubleValue));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new DoubleUnarySeriesExp<>(sExp, UnarySeriesExp.toSeriesOp(Double::parseDouble));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericSeriesExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesExp(left.getName() + "+" + right.getName(),
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 + n2),
                DoubleSeries::add);
    }

    @Override
    public NumericSeriesExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesExp(left.getName() + "-" + right.getName(),
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 - n2),
                DoubleSeries::subtract);
    }

    @Override
    public NumericSeriesExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesExp(left.getName() + "*" + right.getName(),
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 * n2),
                DoubleSeries::multiply);
    }

    @Override
    public NumericSeriesExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesExp(left.getName() + "/" + right.getName(),
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 / n2),
                DoubleSeries::divide);
    }

    @Override
    public NumericSeriesExp<BigDecimal> castAsDecimal(NumericSeriesExp<?> exp, int scale) {
        return new DecimalUnarySeriesExp<>(cast(exp), UnarySeriesExp.toSeriesOp(d -> BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public SeriesCondition lt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesCondition("<",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 < n2),
                DoubleSeries::lt);
    }

    @Override
    public SeriesCondition le(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesCondition("<=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 <= n2),
                DoubleSeries::le);
    }

    @Override
    public SeriesCondition gt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesCondition(">",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 > n2),
                DoubleSeries::gt);
    }

    @Override
    public SeriesCondition ge(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinarySeriesCondition(">=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 >= n2),
                DoubleSeries::ge);
    }
}
