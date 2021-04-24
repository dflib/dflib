package com.nhl.dflib.exp.num;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.NumericExp;
import com.nhl.dflib.exp.func.MapFunction;
import com.nhl.dflib.exp.condition.BinaryCondition;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalExpFactory extends NumericExpFactory {

    protected static Exp<BigDecimal> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(BigDecimal.class)) {
            return (Exp<BigDecimal>) exp;
        }

        if (t.equals(BigInteger.class)) {
            Exp<BigInteger> biExp = (Exp<BigInteger>) exp;
            return new MapFunction<>(biExp, BigDecimal.class, (BigInteger n) -> n != null ? new BigDecimal(n) : null);
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new MapFunction<>(nExp, BigDecimal.class, (Number n) -> n != null ? new BigDecimal(n.doubleValue()) : null);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new MapFunction<>(sExp, BigDecimal.class, (String s) -> s != null ? new BigDecimal(s) : null);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericExp<?> plus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DecimalBinaryExp(left.getName() + "+" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.add(n2)));
    }

    @Override
    public NumericExp<?> minus(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DecimalBinaryExp(left.getName() + "-" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.subtract(n2)));
    }

    @Override
    public NumericExp<?> multiply(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DecimalBinaryExp(left.getName() + "*" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.multiply(n2)));
    }

    @Override
    public NumericExp<?> divide(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new DecimalBinaryExp(left.getName() + "/" + right.getName(),
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.divide(n2)));
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new BinaryCondition("<",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) < 0));
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new BinaryCondition("<=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) <= 0));
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new BinaryCondition(">",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) > 0));
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new BinaryCondition(">=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) >= 0));
    }
}
