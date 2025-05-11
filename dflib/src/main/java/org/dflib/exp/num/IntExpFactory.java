package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.NumExp;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.DoubleAggregators;
import org.dflib.exp.agg.DoubleReduceExp1;
import org.dflib.exp.agg.IntAggregators;
import org.dflib.exp.agg.IntReduceExp1;

import java.math.BigDecimal;


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
    public NumExp<Integer> sum(Exp<? extends Number> exp, Condition filter) {
        return new IntReduceExp1<>("sum", exp, IntAggregators::sum, filter);
    }

    @Override
    public NumExp<?> min(Exp<? extends Number> exp, Condition filter) {
        return new IntReduceExp1<>("min", exp, Min::ofInts, filter);
    }

    @Override
    public NumExp<?> max(Exp<? extends Number> exp, Condition filter) {
        return new IntReduceExp1<>("max", exp, Max::ofInts, filter);
    }

    @Override
    public NumExp<?> avg(Exp<? extends Number> exp, Condition filter) {
        return new DoubleReduceExp1<>("avg", exp, DoubleAggregators::avg, filter);
    }

    @Override
    public NumExp<?> median(Exp<? extends Number> exp, Condition filter) {
        return new DoubleReduceExp1<>("median", exp, s -> Percentiles.ofDoubles(s, 0.5), filter);
    }

    @Override
    public NumExp<?> quantile(Exp<? extends Number> exp, double q, Condition filter) {
        // TODO: display "q" argument in the exp signature
        return new DoubleReduceExp1<>("quantile", exp, s -> Percentiles.ofDoubles(s, q), filter);
    }

    @Override
    public NumExp<?> variance(Exp<? extends Number> exp, boolean usePopulationVariance) {
        return new DoubleReduceExp1<>("variance", exp, s -> DoubleAggregators.variance(s, usePopulationVariance), null);
    }

    @Override
    public NumExp<?> stdDev(Exp<? extends Number> exp, boolean usePopulationStdDev) {
        return new DoubleReduceExp1<>("stdDev", exp, s -> DoubleAggregators.stdDev(s, usePopulationStdDev), null);
    }

    @Override
    public NumExp<Integer> round(Exp<? extends Number> exp) {
        return cast(exp).castAsInt();
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
