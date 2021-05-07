package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;
import com.nhl.dflib.seriesexp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class DecimalExpFactory extends NumericExpFactory {

    protected static SeriesExp<BigDecimal> cast(SeriesExp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(BigDecimal.class)) {
            return (SeriesExp<BigDecimal>) exp;
        }

        if (t.equals(BigInteger.class)) {
            SeriesExp<BigInteger> biExp = (SeriesExp<BigInteger>) exp;
            return new DecimalUnarySeriesExp<>(biExp, UnarySeriesExp.toSeriesOp(BigDecimal::new));
        }

        if (Number.class.isAssignableFrom(t)) {
            SeriesExp<Number> nExp = (SeriesExp<Number>) exp;
            return new DecimalUnarySeriesExp<>(nExp, UnarySeriesExp.toSeriesOp(n -> new BigDecimal(n.doubleValue())));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new DecimalUnarySeriesExp<>(sExp, UnarySeriesExp.toSeriesOp(BigDecimal::new));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericSeriesExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinarySeriesExp("+",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.add(n2)));
    }

    @Override
    public NumericSeriesExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinarySeriesExp("-",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.subtract(n2)));
    }

    @Override
    public NumericSeriesExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinarySeriesExp("*",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.multiply(n2)));
    }

    @Override
    public NumericSeriesExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinarySeriesExp("/",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) ->
                        // TODO: would be nice to be able to specify the result scale explicitly instead of first
                        //  inflating the scale, then trimming trailing zeros. It will not be as slow, and will not
                        //  overflow
                        n1.divide(n2, Math.max(15, 1 + Math.max(n1.scale(), n2.scale())), RoundingMode.HALF_UP).stripTrailingZeros()));
    }

    @Override
    public NumericSeriesExp<BigDecimal> castAsDecimal(NumericSeriesExp<?> exp, int scale) {
        return new DecimalUnarySeriesExp<>(cast(exp), UnarySeriesExp.toSeriesOp(bd -> bd.setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public SeriesCondition lt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new BinarySeriesCondition("<",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) < 0));
    }

    @Override
    public SeriesCondition le(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new BinarySeriesCondition("<=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) <= 0));
    }

    @Override
    public SeriesCondition gt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new BinarySeriesCondition(">",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) > 0));
    }

    @Override
    public SeriesCondition ge(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new BinarySeriesCondition(">=",
                cast(left),
                cast(right),
                BinarySeriesCondition.toSeriesCondition((BigDecimal n1, BigDecimal n2) -> n1.compareTo(n2) >= 0));
    }
}
