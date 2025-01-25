package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.BigIntegerExp;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.ComparableAggregators;
import org.dflib.exp.agg.BigIntegerAggregators;
import org.dflib.exp.agg.BigIntegerReduceExp1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;


public class BigIntegerExpFactory extends NumericExpFactory {

    protected static BigIntegerExp cast(Exp<?> exp) {

        if (exp instanceof BigIntegerExp) {
            return (BigIntegerExp) exp;
        }

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(BigDecimal.class)) {
            Exp<BigDecimal> bdExp = (Exp<BigDecimal>) exp;
            return BigIntegerExp1.mapVal("castAsBigInteger", bdExp, BigDecimal::toBigInteger);
        }

        if (t.equals(BigInteger.class)) {
            Exp<BigInteger> biExp = (Exp<BigInteger>) exp;
            return BigIntegerExp1.mapVal("castAsDecimal", biExp, Function.identity());
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return BigIntegerExp1.mapVal("castAsBigInteger", nExp, n -> BigDecimal.valueOf(n.doubleValue()).toBigInteger());
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return BigIntegerExp1.mapVal("castAsBigInteger", sExp, BigInteger::new);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public BigIntegerExp add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigIntegerExp2.mapVal("+",
                cast(left),
                cast(right),
                BigInteger::add);
    }

    @Override
    public BigIntegerExp sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigIntegerExp2.mapVal("-",
                cast(left),
                cast(right),
                BigInteger::subtract);
    }

    @Override
    public BigIntegerExp mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigIntegerExp2.mapVal("*",
                cast(left),
                cast(right),
                BigInteger::multiply);
    }

    @Override
    public BigIntegerExp div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigIntegerExp2.mapVal("/",
                cast(left),
                cast(right),
                BigInteger::divide);
    }

    @Override
    public BigIntegerExp mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigIntegerExp2.mapVal("%",
                cast(left),
                cast(right),
                BigInteger::remainder);
    }

    @Override
    public BigIntegerExp abs(Exp<? extends Number> exp) {
        return BigIntegerExp1.mapVal("abs", cast(exp), BigInteger::abs);
    }

    @Override
    public BigIntegerExp castAsBigInteger(NumExp<?> exp) {
        return cast(exp);
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return DecimalExpFactory.cast(exp);
    }

    @Override
    public BigIntegerExp cumSum(Exp<? extends Number> exp) {
        return BigIntegerExp1.map("cumSum", cast(exp), BigIntegerAggregators::cumSum);
    }

    @Deprecated
    @Override
    public BigIntegerExp sum(Exp<? extends Number> exp) {
        return sum(exp, null);
    }

    @Override
    public BigIntegerExp sum(Exp<? extends Number> exp, Condition filter) {
        return new BigIntegerReduceExp1<>("sum", cast(exp), BigIntegerAggregators::sum, filter);
    }

    @Deprecated
    @Override
    public BigIntegerExp min(Exp<? extends Number> exp) {
        return min(exp, null);
    }

    @Override
    public BigIntegerExp min(Exp<? extends Number> exp, Condition filter) {
        return new BigIntegerReduceExp1<>("min", cast(exp), ComparableAggregators::min, filter);
    }

    @Deprecated
    @Override
    public BigIntegerExp max(Exp<? extends Number> exp) {
        return max(exp, null);
    }

    @Override
    public BigIntegerExp max(Exp<? extends Number> exp, Condition filter) {
        return new BigIntegerReduceExp1<>("max", cast(exp), ComparableAggregators::max, filter);
    }

    @Deprecated
    @Override
    public BigIntegerExp avg(Exp<? extends Number> exp) {
        return avg(exp, null);
    }

    @Override
    public BigIntegerExp avg(Exp<? extends Number> exp, Condition filter) {
        // TODO
        throw new UnsupportedOperationException("TODO: support for BigInteger.avg");
    }

    @Deprecated
    @Override
    public BigIntegerExp median(Exp<? extends Number> exp) {
        return median(exp, null);
    }

    @Override
    public BigIntegerExp median(Exp<? extends Number> exp, Condition filter) {
        return new BigIntegerReduceExp1<>("median", cast(exp), s -> Percentiles.ofBigIntegers(s, 0.5), filter);
    }

    @Override
    public BigIntegerExp quantile(Exp<? extends Number> exp, double q, Condition filter) {
        return new BigIntegerReduceExp1<>("quantile", cast(exp), s -> Percentiles.ofBigIntegers(s, q), filter);
    }

    @Override
    public BigIntegerExp round(Exp<? extends Number> exp) {
        return cast(exp);
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        // TODO: should we apply ".stripTrailingZeros()" for consistency, but at the expense of performance?
        return MapCondition2.mapVal("=", cast(left), cast(right), BigInteger::equals);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return MapCondition2.mapVal("!=", cast(left), cast(right), (n1, n2) -> !n1.equals(n2));
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return MapCondition2.mapVal("<", cast(left), cast(right), (n1, n2) -> n1.compareTo(n2) < 0);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return MapCondition2.mapVal("<=", cast(left), cast(right), (n1, n2) -> n1.compareTo(n2) <= 0);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return MapCondition2.mapVal(">", cast(left), cast(right), (n1, n2) -> n1.compareTo(n2) > 0);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return MapCondition2.mapVal(">=", cast(left), cast(right), (n1, n2) -> n1.compareTo(n2) >= 0);
    }

    @Override
    public Condition between(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return MapCondition3.mapVal("between", "and", cast(left), cast(from), cast(to), (n1, n2, n3) -> n1.compareTo(n2) >= 0 && n1.compareTo(n3) <= 0);
    }
}
