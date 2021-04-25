package com.nhl.dflib.exp.num;

import com.nhl.dflib.Condition;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.*;
import com.nhl.dflib.exp.condition.BinaryCondition;

public class DoubleExpFactory extends NumericExpFactory {

    protected static Exp<Double> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Double.class)) {
            return (Exp<Double>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new UnaryExp<>(nExp, Double.class, (Number n) -> n != null ? n.doubleValue() : null);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new UnaryExp<>(sExp, Double.class, (String s) -> s != null ? Double.parseDouble(s) : null);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericExp<?> plus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp(left.getName() + "+" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 + n2),
                DoubleSeries::plus);
    }

    @Override
    public NumericExp<?> minus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp(left.getName() + "-" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 - n2),
                DoubleSeries::minus);
    }

    @Override
    public NumericExp<?> multiply(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp(left.getName() + "*" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 * n2),
                DoubleSeries::multiply);
    }

    @Override
    public NumericExp<?> divide(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DoubleBinaryExp(left.getName() + "/" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Double n1, Double n2) -> n1 / n2),
                DoubleSeries::divide);
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
