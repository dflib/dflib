package com.nhl.dflib.exp.num;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.NumericExp;
import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.condition.BinaryCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LongExpFactory extends NumericExpFactory {

    protected static Exp<Long> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Long.class)) {
            return (Exp<Long>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new LongUnaryExp<>(nExp, UnaryExp.toSeriesOp(Number::longValue));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new LongUnaryExp<>(sExp, UnaryExp.toSeriesOp(Long::parseLong));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Long");
    }

    @Override
    public NumericExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp(left.getName() + "+" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 + n2),
                LongSeries::add);
    }

    @Override
    public NumericExp<?> subtract(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp(left.getName() + "-" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 - n2),
                LongSeries::subtract);
    }

    @Override
    public NumericExp<?> multiply(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp(left.getName() + "*" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 * n2),
                LongSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp(left.getName() + "/" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 / n2),
                LongSeries::divide);
    }

    @Override
    public NumericExp<?> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnaryExp.toSeriesOp(l -> BigDecimal.valueOf(l).setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryCondition("<",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Long n1, Long n2) -> n1 < n2),
                LongSeries::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryCondition("<=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Long n1, Long n2) -> n1 <= n2),
                LongSeries::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryCondition(">",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Long n1, Long n2) -> n1 > n2),
                LongSeries::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryCondition(">=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Long n1, Long n2) -> n1 >= n2),
                LongSeries::ge);
    }
}
