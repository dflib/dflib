package com.nhl.dflib.exp.num;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.exp.*;
import com.nhl.dflib.exp.condition.BinaryCondition;
import com.nhl.dflib.exp.func.MapFunction;

public class LongExpFactory extends NumericExpFactory {

    protected static Exp<Long> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Long.class)) {
            return (Exp<Long>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new MapFunction<>(nExp, Long.class, (Number n) -> n != null ? n.longValue() : null);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new MapFunction<>(sExp, Long.class, (String s) -> s != null ? Long.parseLong(s) : null);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Long");
    }

    @Override
    public NumericExp<?> plus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp(left.getName() + "+" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 + n2),
                LongSeries::plus);
    }

    @Override
    public NumericExp<?> minus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp(left.getName() + "-" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 - n2),
                LongSeries::minus);
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
