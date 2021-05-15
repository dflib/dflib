package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.agg.IntExpAggregator;
import com.nhl.dflib.exp.condition.BinaryCondition;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class IntExpFactory extends NumericExpFactory {

    protected static Exp<Integer> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
            return (Exp<Integer>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new IntUnaryExp<>("castAsInt", nExp, UnaryExp.toSeriesOp(Number::intValue));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new IntUnaryExp<>("castAsInt", sExp, UnaryExp.toSeriesOp(Integer::parseInt));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Integer");
    }

    @Override
    public NumericExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("+",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 + n2),
                IntSeries::add);
    }

    @Override
    public NumericExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("-",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 - n2),
                IntSeries::sub);
    }

    @Override
    public NumericExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("*",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 * n2),
                IntSeries::mul);
    }

    @Override
    public NumericExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("/",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 / n2),
                IntSeries::div);
    }

    @Override
    public NumericExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp("%",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 % n2),
                IntSeries::mod);
    }

    @Override
    public DecimalExp castAsDecimal(NumericExp<?> exp) {
        return new DecimalUnaryExp<>("castAsDecimal", cast(exp), UnaryExp.toSeriesOp(i -> BigDecimal.valueOf((long) i)));
    }

    @Override
    public NumericExp<Integer> sum(Exp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.sumInt());
    }

    @Override
    public NumericExp<?> min(Exp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.minInt());
    }

    @Override
    public NumericExp<?> max(Exp<? extends Number> exp) {
        return new IntExpAggregator<>(exp, AggregatorFunctions.maxInt());
    }

    @Override
    public NumericExp<?> avg(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.avgDouble());
    }

    @Override
    public NumericExp<?> median(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, AggregatorFunctions.medianDouble());
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryCondition("=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition(Integer::equals),
                IntSeries::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryCondition("!=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Integer n1, Integer n2) -> !n1.equals(n2)),
                IntSeries::ne);
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryCondition("<",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 < n2),
                IntSeries::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryCondition("<=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 <= n2),
                IntSeries::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryCondition(">",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 > n2),
                IntSeries::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryCondition(">=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Integer n1, Integer n2) -> n1 >= n2),
                IntSeries::ge);
    }
}
