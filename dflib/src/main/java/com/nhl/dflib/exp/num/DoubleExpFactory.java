package com.nhl.dflib.exp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;
import com.nhl.dflib.exp.UnarySeriesExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

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
            return new DoubleUnaryExp<>(nExp, UnarySeriesExp.toSeriesOp(Number::doubleValue));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new DoubleUnaryExp<>(sExp, UnarySeriesExp.toSeriesOp(Double::parseDouble));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinaryExp("+",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 + n2),
                DoubleSeries::add);
    }

    @Override
    public NumericExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinaryExp("-",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 - n2),
                DoubleSeries::subtract);
    }

    @Override
    public NumericExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinaryExp("*",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 * n2),
                DoubleSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinaryExp("/",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 / n2),
                DoubleSeries::divide);
    }

    @Override
    public NumericExp<?> mod(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DoubleBinaryExp("%",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((Double n1, Double n2) -> n1 % n2),
                DoubleSeries::mod);
    }

    @Override
    public NumericExp<BigDecimal> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnarySeriesExp.toSeriesOp(d -> BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericExp<Double> sum(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.sumDouble());
    }

    @Override
    public NumericExp<?> min(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.minDouble());
    }

    @Override
    public NumericExp<?> max(SeriesExp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.maxDouble());
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
