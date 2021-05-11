package com.nhl.dflib.exp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleExpFactory extends NumericExpFactory {

    protected static Exp<Double> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Double.class)) {
            return (Exp<Double>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new DoubleUnaryExp<>(nExp, UnaryExp.toSeriesOp(Number::doubleValue));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new DoubleUnaryExp<>(sExp, UnaryExp.toSeriesOp(Double::parseDouble));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("+",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 + n2),
                DoubleSeries::add);
    }

    @Override
    public NumericExp<?> subtract(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("-",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 - n2),
                DoubleSeries::subtract);
    }

    @Override
    public NumericExp<?> multiply(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("*",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 * n2),
                DoubleSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("/",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 / n2),
                DoubleSeries::divide);
    }

    @Override
    public NumericExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("%",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 % n2),
                DoubleSeries::mod);
    }

    @Override
    public NumericExp<BigDecimal> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnaryExp.toSeriesOp(d -> BigDecimal.valueOf(d).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericExp<Double> sum(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.sumDouble());
    }

    @Override
    public NumericExp<?> min(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.minDouble());
    }

    @Override
    public NumericExp<?> max(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.maxDouble());
    }

    @Override
    public NumericExp<?> avg(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.averageDouble());
    }

    @Override
    public NumericExp<?> median(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.medianDouble());
    }

    @Override
    public SeriesCondition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinarySeriesCondition("<",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 < n2),
                DoubleSeries::lt);
    }

    @Override
    public SeriesCondition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinarySeriesCondition("<=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 <= n2),
                DoubleSeries::le);
    }

    @Override
    public SeriesCondition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinarySeriesCondition(">",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 > n2),
                DoubleSeries::gt);
    }

    @Override
    public SeriesCondition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinarySeriesCondition(">=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((Double n1, Double n2) -> n1 >= n2),
                DoubleSeries::ge);
    }
}
