package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.FloatSeries;
import org.dflib.NumExp;
import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.DoubleReduceExp1;
import org.dflib.exp.agg.FloatAggregators;
import org.dflib.exp.agg.FloatReduceExp1;

import java.math.BigDecimal;

/**
 * @since 1.1.0
 */
public class FloatExpFactory extends NumericExpFactory {

    protected static Exp<Float> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(Float.class)) {
            return (Exp<Float>) exp;
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return FloatExp1.mapVal("castAsFloat", nExp, Number::floatValue);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return FloatExp1.mapVal("castAsFloat", sExp, Float::parseFloat);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Float");
    }

    @Override
    public NumExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatExp2.mapVal("+",
                cast(left),
                cast(right),
                (n1, n2) -> n1 + n2,
                FloatSeries::add);
    }

    @Override
    public NumExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatExp2.mapVal("-",
                cast(left),
                cast(right),
                (n1, n2) -> n1 - n2,
                FloatSeries::sub);
    }

    @Override
    public NumExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatExp2.mapVal("*",
                cast(left),
                cast(right),
                (n1, n2) -> n1 * n2,
                FloatSeries::mul);
    }

    @Override
    public NumExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatExp2.mapVal("/",
                cast(left),
                cast(right),
                (n1, n2) -> n1 / n2,
                FloatSeries::div);
    }

    @Override
    public NumExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatExp2.mapVal("%",
                cast(left),
                cast(right),
                (n1, n2) -> n1 % n2,
                FloatSeries::mod);
    }

    @Override
    public NumExp<?> abs(Exp<? extends Number> exp) {
        return FloatExp1.mapVal("abs", cast(exp), Math::abs);
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return DecimalExp1.mapVal("castAsDecimal", cast(exp), BigDecimal::valueOf);
    }

    @Override
    public NumExp<Double> cumSum(Exp<? extends Number> exp) {
        return DoubleExp1.map("cumSum", exp, FloatAggregators::cumSum);
    }

    @Override
    public NumExp<Double> sum(Exp<? extends Number> exp, Condition filter) {
        return new DoubleReduceExp1<>("sum", exp, FloatAggregators::sum, filter);
    }

    @Override
    public NumExp<?> min(Exp<? extends Number> exp, Condition filter) {
        return new FloatReduceExp1<>("min", exp, FloatAggregators::min, filter);
    }

    @Override
    public NumExp<?> max(Exp<? extends Number> exp, Condition filter) {
        return new FloatReduceExp1<>("max", exp, FloatAggregators::max, filter);
    }

    @Override
    public NumExp<?> avg(Exp<? extends Number> exp, Condition filter) {
        return new FloatReduceExp1<>("avg", exp, FloatAggregators::avg, filter);
    }

    @Override
    public NumExp<?> median(Exp<? extends Number> exp, Condition filter) {
        return new FloatReduceExp1<>("median", exp,  s -> Percentiles.ofFloats(s, 0.5), filter);
    }

    @Override
    public NumExp<?> quantile(Exp<? extends Number> exp, double q, Condition filter) {
        // TODO: display "q" argument in the exp signature
        return new FloatReduceExp1<>("quantile", exp, s -> Percentiles.ofFloats(s, q), filter);
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatCondition2.mapVal("=", cast(left), cast(right), Float::equals, FloatSeries::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatCondition2.mapVal("!=", cast(left), cast(right), (n1, n2) -> !n1.equals(n2), FloatSeries::ne);
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatCondition2.mapVal("<", cast(left), cast(right), (n1, n2) -> n1 < n2, FloatSeries::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatCondition2.mapVal("<=", cast(left), cast(right), (n1, n2) -> n1 <= n2, FloatSeries::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatCondition2.mapVal(">", cast(left), cast(right), (n1, n2) -> n1 > n2, FloatSeries::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return FloatCondition2.mapVal(">=", cast(left), cast(right), (n1, n2) -> n1 >= n2, FloatSeries::ge);
    }

    @Override
    public Condition between(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return FloatCondition3.mapVal(
                "between",
                "and",
                cast(left),
                cast(from),
                cast(to),
                (n1, n2, n3) -> n1 >= n2 && n1 <= n3,
                FloatSeries::between);
    }
}
