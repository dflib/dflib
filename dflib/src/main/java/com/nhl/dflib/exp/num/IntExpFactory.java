package com.nhl.dflib.exp.num;

import com.nhl.dflib.Condition;
import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.exp.agg.DoubleAggregators;
import com.nhl.dflib.exp.agg.DoubleExpAggregator;
import com.nhl.dflib.exp.agg.IntAggregators;
import com.nhl.dflib.exp.agg.IntExpAggregator;

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
            return IntExp1.mapVal("castAsInt", nExp, Number::intValue);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return IntExp1.mapVal("castAsInt", sExp, Integer::parseInt);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Integer");
    }

    @Override
    public NumExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntExp2.mapVal("+", cast(left), cast(right), (n1, n2) -> n1 + n2, IntSeries::add);
    }

    @Override
    public NumExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntExp2.mapVal("-", cast(left), cast(right), (n1, n2) -> n1 - n2, IntSeries::sub);
    }

    @Override
    public NumExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntExp2.mapVal("*", cast(left), cast(right), (n1, n2) -> n1 * n2, IntSeries::mul);
    }

    @Override
    public NumExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntExp2.mapVal("/", cast(left), cast(right), (n1, n2) -> n1 / n2, IntSeries::div);
    }

    @Override
    public NumExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntExp2.mapVal("%", cast(left), cast(right), (n1, n2) -> n1 % n2, IntSeries::mod);
    }

    @Override
    public NumExp<?> abs(Exp<? extends Number> exp) {
        return IntExp1.mapVal("abs", cast(exp), Math::abs);
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return DecimalExp1.mapVal("castAsDecimal", cast(exp), BigDecimal::valueOf);
    }

    @Override
    public NumExp<Long> cumSum(Exp<? extends Number> exp) {
        return LongExp1.map("cumSum", exp, IntAggregators::cumSum);
    }

    @Override
    public NumExp<Integer> sum(Exp<? extends Number> exp) {
        return new IntExpAggregator<>("sum", exp, IntAggregators::sum);
    }

    @Override
    public NumExp<?> min(Exp<? extends Number> exp) {
        return new IntExpAggregator<>("min", exp, IntAggregators::min);
    }

    @Override
    public NumExp<?> max(Exp<? extends Number> exp) {
        return new IntExpAggregator<>("max", exp, IntAggregators::max);
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
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntCondition2.mapVal("=", cast(left), cast(right), Integer::equals, IntSeries::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntCondition2.mapVal("!=", cast(left), cast(right), (n1, n2) -> !n1.equals(n2), IntSeries::ne);
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntCondition2.mapVal("<", cast(left), cast(right), (n1, n2) -> n1 < n2, IntSeries::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntCondition2.mapVal("<=", cast(left), cast(right), (n1, n2) -> n1 <= n2, IntSeries::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntCondition2.mapVal(">", cast(left), cast(right), (n1, n2) -> n1 > n2, IntSeries::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return IntCondition2.mapVal(">=", cast(left), cast(right), (n1, n2) -> n1 >= n2, IntSeries::ge);
    }

    @Override
    public Condition between(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return IntCondition3.mapVal(
                "between",
                "and",
                cast(left),
                cast(from),
                cast(to),
                (n1, n2, n3) -> n1 >= n2 && n1 <= n3,
                IntSeries::between);
    }
}
