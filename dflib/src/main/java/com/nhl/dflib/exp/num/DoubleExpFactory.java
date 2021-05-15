package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.agg.DoubleAggregators;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.condition.BinaryCondition;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class DoubleExpFactory extends NumericExpFactory {

    protected static Exp<Double> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Double.class)) {
            return (Exp<Double>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new DoubleUnaryExp<>("castAsDouble", nExp, UnaryExp.toSeriesOp(Number::doubleValue));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new DoubleUnaryExp<>("castAsDouble", sExp, UnaryExp.toSeriesOp(Double::parseDouble));
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
    public NumericExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("-",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 - n2),
                DoubleSeries::sub);
    }

    @Override
    public NumericExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("*",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 * n2),
                DoubleSeries::mul);
    }

    @Override
    public NumericExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp("/",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 / n2),
                DoubleSeries::div);
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
    public DecimalExp castAsDecimal(NumericExp<?> exp) {
        return new DecimalUnaryExp<>("castAsDecimal", cast(exp), UnaryExp.toSeriesOp(d -> BigDecimal.valueOf(d)));
    }

    @Override
    public NumericExp<Double> sum(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::sum);
    }

    @Override
    public NumericExp<?> min(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::min);
    }

    @Override
    public NumericExp<?> max(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::max);
    }

    @Override
    public NumericExp<?> avg(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::avg);
    }

    @Override
    public NumericExp<?> median(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::median);
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryCondition("=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition(Double::equals),
                DoubleSeries::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryCondition("!=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Double n1, Double n2) -> !n1.equals(n2)),
                DoubleSeries::ne);
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryCondition("<",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Double n1, Double n2) -> n1 < n2),
                DoubleSeries::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryCondition("<=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Double n1, Double n2) -> n1 <= n2),
                DoubleSeries::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryCondition(">",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Double n1, Double n2) -> n1 > n2),
                DoubleSeries::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryCondition(">=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Double n1, Double n2) -> n1 >= n2),
                DoubleSeries::ge);
    }
}
