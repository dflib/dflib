package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.LongSeries;
import org.dflib.NumExp;
import org.dflib.exp.agg.DoubleAggregators;
import org.dflib.exp.agg.DoubleExpAggregator;
import org.dflib.exp.agg.LongAggregators;
import org.dflib.exp.agg.LongExpAggregator;

import java.math.BigDecimal;


public class LongExpFactory extends NumericExpFactory {

    protected static Exp<Long> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Long.class)) {
            return (Exp<Long>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return LongExp1.mapVal("castAsLong", nExp, Number::longValue);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return LongExp1.mapVal("castAsLong", sExp, Long::parseLong);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Long");
    }

    @Override
    public NumExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongExp2.mapVal("+", cast(left), cast(right), (n1, n2) -> n1 + n2, LongSeries::add);
    }

    @Override
    public NumExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongExp2.mapVal("-", cast(left), cast(right), (n1, n2) -> n1 - n2, LongSeries::sub);
    }

    @Override
    public NumExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongExp2.mapVal("*", cast(left), cast(right), (n1, n2) -> n1 * n2, LongSeries::mul);
    }

    @Override
    public NumExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongExp2.mapVal("/", cast(left), cast(right), (n1, n2) -> n1 / n2, LongSeries::div);
    }

    @Override
    public NumExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongExp2.mapVal("%", cast(left), cast(right), (n1, n2) -> n1 % n2, LongSeries::mod);
    }

    @Override
    public NumExp<?> abs(Exp<? extends Number> exp) {
        return LongExp1.mapVal("abs", cast(exp), Math::abs);
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return DecimalExp1.mapVal("castAsDecimal", cast(exp), BigDecimal::valueOf);
    }

    @Override
    public NumExp<Long> cumSum(Exp<? extends Number> exp) {
        return LongExp1.map("cumSum", exp, LongAggregators::cumSum);
    }

    @Override
    public NumExp<Long> sum(Exp<? extends Number> exp) {
        return new LongExpAggregator<>("sum", exp, LongAggregators::sum);
    }

    @Override
    public NumExp<?> min(Exp<? extends Number> exp) {
        return new LongExpAggregator<>("min", exp, LongAggregators::min);
    }

    @Override
    public NumExp<?> max(Exp<? extends Number> exp) {
        return new LongExpAggregator<>("max", exp, LongAggregators::max);
    }

    @Override
    public NumExp<?> avg(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>("avg", exp, DoubleAggregators::avg);
    }

    @Override
    public NumExp<?> median(Exp<? extends Number> exp) {
        return new DoubleExpAggregator<>("median", exp, DoubleAggregators::median);
    }

    @Override
    public NumExp<?> variance(Exp<? extends Number> exp, boolean usePopulationVariance) {
        return new DoubleExpAggregator<>("variance", exp, s -> DoubleAggregators.variance(s, usePopulationVariance));
    }

    @Override
    public NumExp<?> stdDev(Exp<? extends Number> exp, boolean usePopulationStdDev) {
        return new DoubleExpAggregator<>("variance", exp, s -> DoubleAggregators.stdDev(s, usePopulationStdDev));
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongCondition2.mapVal("=", cast(left), cast(right), Long::equals, LongSeries::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongCondition2.mapVal("!=", cast(left), cast(right), (n1, n2) -> !n1.equals(n2), LongSeries::ne);
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongCondition2.mapVal("<", cast(left), cast(right), (n1, n2) -> n1 < n2, LongSeries::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongCondition2.mapVal("<=", cast(left), cast(right), (n1, n2) -> n1 <= n2, LongSeries::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongCondition2.mapVal(">", cast(left), cast(right), (n1, n2) -> n1 > n2, LongSeries::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return LongCondition2.mapVal(">=", cast(left), cast(right), (n1, n2) -> n1 >= n2, LongSeries::ge);
    }

    @Override
    public Condition between(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return LongCondition3.mapVal(
                "between",
                "and",
                cast(left),
                cast(from),
                cast(to),
                (n1, n2, n3) -> n1 >= n2 && n1 <= n3,
                LongSeries::between);
    }
}
