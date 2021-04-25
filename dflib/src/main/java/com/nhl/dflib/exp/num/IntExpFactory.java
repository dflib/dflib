package com.nhl.dflib.exp.num;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.exp.*;
import com.nhl.dflib.exp.condition.BinaryCondition;

public class IntExpFactory extends NumericExpFactory {

    protected static Exp<Integer> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Integer.class) || t.equals(Integer.TYPE)) {
            return (Exp<Integer>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new UnaryExp<>(nExp, Integer.class, (Number n) -> n != null ? n.intValue() : null);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new UnaryExp<>(sExp, Integer.class, (String s) -> s != null ? Integer.parseInt(s) : null);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Integer");
    }

    @Override
    public NumericExp<?> plus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp(left.getName() + "+" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 + n2),
                IntSeries::plus);
    }

    @Override
    public NumericExp<?> minus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp(left.getName() + "-" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 - n2),
                IntSeries::minus);
    }

    @Override
    public NumericExp<?> multiply(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp(left.getName() + "*" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 * n2),
                IntSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new IntBinaryExp(left.getName() + "/" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Integer n1, Integer n2) -> n1 / n2),
                IntSeries::divide);
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
