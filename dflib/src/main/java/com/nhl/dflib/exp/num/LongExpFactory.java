package com.nhl.dflib.exp.num;

import com.nhl.dflib.*;
import com.nhl.dflib.exp.BinaryExp;
import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.agg.*;
import com.nhl.dflib.exp.condition.BinaryCondition;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class LongExpFactory extends NumericExpFactory {

    protected static Exp<Long> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Long.class)) {
            return (Exp<Long>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return new LongUnaryExp<>("castAsLong", nExp, UnaryExp.toSeriesOp(Number::longValue));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return new LongUnaryExp<>("castAsLong", sExp, UnaryExp.toSeriesOp(Long::parseLong));
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Long");
    }

    @Override
    public NumExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp("+",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 + n2),
                LongSeries::add);
    }

    @Override
    public NumExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp("-",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 - n2),
                LongSeries::sub);
    }

    @Override
    public NumExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp("*",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 * n2),
                LongSeries::mul);
    }

    @Override
    public NumExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp("/",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 / n2),
                LongSeries::div);
    }

    @Override
    public NumExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryExp("%",
                cast(left),
                cast(right),
                BinaryExp.toSeriesOp((Long n1, Long n2) -> n1 % n2),
                LongSeries::mod);
    }


    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return new DecimalUnaryExp<>("castAsDecimal", cast(exp), UnaryExp.toSeriesOp(l -> BigDecimal.valueOf(l)));
    }

    @Override
    public NumExp<Long> sum(Exp<? extends Number> exp) {
        return new LongExpAggregator<>(exp, LongAggregators::sum);
    }

    @Override
    public NumExp<?> min(Exp<? extends Number> exp) {
        return new LongExpAggregator<>(exp, LongAggregators::min);
    }

    @Override
    public NumExp<?> max(Exp<? extends Number> exp) {
        return new LongExpAggregator<>(exp, LongAggregators::max);
    }

    @Override
    public NumExp<?> avg(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::avg);
    }

    @Override
    public NumExp<?> median(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>(exp, DoubleAggregators::median);
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryCondition("=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition(Long::equals),
                LongSeries::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new LongBinaryCondition("!=",
                cast(left),
                cast(right),
                BinaryCondition.toSeriesCondition((Long n1, Long n2) -> !n1.equals(n2)),
                LongSeries::ne);
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
