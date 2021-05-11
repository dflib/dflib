package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumericExp;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.BinarySeriesExp;
import com.nhl.dflib.exp.UnarySeriesExp;
import com.nhl.dflib.exp.agg.AggregatorFunctions;
import com.nhl.dflib.exp.agg.DecimalExpAggregator;
import com.nhl.dflib.exp.condition.BinarySeriesCondition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecimalExpFactory extends NumericExpFactory {

    private static final MathContext divisionContext(BigDecimal n1, BigDecimal n2) {
        return new MathContext(Math.max(15, 1 + Math.max(n1.scale(), n2.scale())), RoundingMode.HALF_UP);
    }

    protected static SeriesExp<BigDecimal> cast(SeriesExp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(BigDecimal.class)) {
            return (SeriesExp<BigDecimal>) exp;
        }

        if (t.equals(BigInteger.class)) {
            SeriesExp<BigInteger> biExp = (SeriesExp<BigInteger>) exp;
            return new DecimalUnaryExp<>(biExp, UnarySeriesExp.toSeriesOp(BigDecimal::new));
        }

        if (Number.class.isAssignableFrom(t)) {
            SeriesExp<Number> nExp = (SeriesExp<Number>) exp;
            return new DecimalUnaryExp<>(nExp, UnarySeriesExp.toSeriesOp(n -> new BigDecimal(n.doubleValue())));
        }

        if (t.equals(String.class)) {
            SeriesExp<String> sExp = (SeriesExp<String>) exp;
            return new DecimalUnaryExp<>(sExp, UnarySeriesExp.toSeriesOp(BigDecimal::new));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public NumericExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinaryExp("+",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.add(n2)));
    }

    @Override
    public NumericExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinaryExp("-",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.subtract(n2)));
    }

    @Override
    public NumericExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinaryExp("*",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) -> n1.multiply(n2)));
    }

    @Override
    public NumericExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinaryExp("/",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) ->
                        // TODO: would be nice to be able to specify the result scale explicitly instead of first
                        //  inflating the scale, then trimming trailing zeros. It will not be as slow, and will not
                        //  overflow
                        n1.divide(n2, divisionContext(n1, n2)).stripTrailingZeros()));
    }

    @Override
    public NumericExp<?> mod(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return new DecimalBinaryExp("%",
                cast(left),
                cast(right),
                BinarySeriesExp.toSeriesOp((BigDecimal n1, BigDecimal n2) ->

                        // TODO: would be nice to be able to specify the result scale explicitly instead of first
                        //  inflating the scale, then trimming trailing zeros. It will not be as slow, and will not
                        //  overflow

                        // TODO: result sign handling in a manner compatible with "%"
                        n1.remainder(n2, divisionContext(n1, n2)).stripTrailingZeros()));
    }

    @Override
    public NumericExp<BigDecimal> castAsDecimal(NumericExp<?> exp, int scale) {
        return new DecimalUnaryExp<>(cast(exp), UnarySeriesExp.toSeriesOp(bd -> bd.setScale(scale, RoundingMode.HALF_UP)));
    }

    @Override
    public NumericExp<BigDecimal> sum(SeriesExp<? extends Number> exp) {
        return new DecimalExpAggregator(cast(exp), AggregatorFunctions.sumDecimal());
    }

    @Override
    public NumericExp<?> min(SeriesExp<? extends Number> exp) {
        return new DecimalExpAggregator(cast(exp), AggregatorFunctions.min());
    }

    @Override
    public NumericExp<?> max(SeriesExp<? extends Number> exp) {
        return new DecimalExpAggregator(cast(exp), AggregatorFunctions.max());
    }

    @Override
    public NumericExp<?> avg(SeriesExp<? extends Number> exp) {
        throw new UnsupportedOperationException("TODO: support for BigDecimal.avg");
    }

    @Override
    public NumericExp<?> median(SeriesExp<? extends Number> exp) {
        throw new UnsupportedOperationException("TODO: support for BigDecimal.median");
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
