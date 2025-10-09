package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.agg.Average;
import org.dflib.agg.CumSum;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.Sum;
import org.dflib.exp.agg.BigintReduceExp1;
import org.dflib.exp.agg.DecimalReduceExp1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @since 2.0.0
 */
public class BigintExpFactory extends NumericExpFactory {

    protected static Exp<BigInteger> cast(Exp<?> exp) {

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(BigInteger.class)) {
            return (Exp<BigInteger>) exp;
        }

        if (t.equals(BigDecimal.class)) {
            Exp<BigDecimal> bdExp = (Exp<BigDecimal>) exp;
            return BigintExp1.mapVal("castAsBigint", bdExp, BigDecimal::toBigInteger);
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return BigintExp1.mapVal("castAsBigint", nExp, n -> BigInteger.valueOf(n.longValue()));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return BigintExp1.mapVal("castAsBigint", sExp, BigInteger::new);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to 'bigint'");
    }

    @Override
    public NumExp<BigInteger> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigintExp2.mapVal("+",
                cast(left),
                cast(right),
                BigInteger::add);
    }

    @Override
    public NumExp<BigInteger> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigintExp2.mapVal("-",
                cast(left),
                cast(right),
                BigInteger::subtract);
    }

    @Override
    public NumExp<BigInteger> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigintExp2.mapVal("*",
                cast(left),
                cast(right),
                BigInteger::multiply);
    }

    @Override
    public NumExp<BigInteger> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigintExp2.mapVal("/",
                cast(left),
                cast(right),
                BigInteger::divide);
    }

    @Override
    public NumExp<BigInteger> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return BigintExp2.mapVal("%",
                cast(left),
                cast(right),
                BigInteger::remainder);
    }

    @Override
    public NumExp<BigInteger> abs(Exp<? extends Number> exp) {
        return BigintExp1.mapVal("abs", cast(exp), BigInteger::abs);
    }

    @Override
    public NumExp<BigInteger> castAsBigint(NumExp<?> exp) {
        return (NumExp<BigInteger>) cast(exp);
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return DecimalExpFactory.cast(exp);
    }

    @Override
    public NumExp<BigInteger> cumSum(Exp<? extends Number> exp) {
        return BigintExp1.map("cumSum", cast(exp), CumSum::ofBigints);
    }

    @Deprecated
    @Override
    public NumExp<BigInteger> sum(Exp<? extends Number> exp) {
        return sum(exp, null);
    }

    @Override
    public NumExp<BigInteger> sum(Exp<? extends Number> exp, Condition filter) {
        return new BigintReduceExp1<>("sum", cast(exp), Sum::ofBigints, filter);
    }

    @Deprecated
    @Override
    public NumExp<BigInteger> min(Exp<? extends Number> exp) {
        return min(exp, null);
    }

    @Override
    public NumExp<BigInteger> min(Exp<? extends Number> exp, Condition filter) {
        return new BigintReduceExp1<>("min", cast(exp), Min::ofComparables, filter);
    }

    @Deprecated
    @Override
    public NumExp<BigInteger> max(Exp<? extends Number> exp) {
        return max(exp, null);
    }

    @Override
    public NumExp<BigInteger> max(Exp<? extends Number> exp, Condition filter) {
        return new BigintReduceExp1<>("max", cast(exp), Max::ofComparables, filter);
    }

    @Override
    public NumExp<BigDecimal> avg(Exp<? extends Number> exp, Condition filter) {
        return new DecimalReduceExp1<>("avg", cast(exp), Average::ofBigints, filter);
    }

    @Override
    public DecimalExp median(Exp<? extends Number> exp, Condition filter) {
        return new DecimalReduceExp1<>("median", cast(exp), s -> Percentiles.ofBigints(s, 0.5), filter);
    }

    @Override
    public DecimalExp quantile(Exp<? extends Number> exp, double q, Condition filter) {
        return new DecimalReduceExp1<>("quantile", cast(exp), s -> Percentiles.ofBigints(s, q), filter);
    }

    @Override
    public NumExp<?> variance(Exp<? extends Number> exp, boolean usePopulationVariance) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public NumExp<?> stdDev(Exp<? extends Number> exp, boolean usePopulationStdDev) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public NumExp<BigInteger> round(Exp<? extends Number> exp) {
        return (NumExp<BigInteger>) cast(exp);
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

    @Override
    public Condition notBetween(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return MapCondition3.mapVal("notBetween", "and", cast(left), cast(from), cast(to), (n1, n2, n3) -> n1.compareTo(n2) < 0 || n1.compareTo(n3) > 0);
    }
}
