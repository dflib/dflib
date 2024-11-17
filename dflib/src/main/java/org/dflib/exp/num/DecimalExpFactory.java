package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.ComparableAggregators;
import org.dflib.exp.agg.DecimalAggregators;
import org.dflib.exp.agg.DecimalReduceExp1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;


public class DecimalExpFactory extends NumericExpFactory {

    private static MathContext divisionContext(BigDecimal n1, BigDecimal n2) {
        return new MathContext(Math.max(15, 1 + Math.max(n1.scale(), n2.scale())), RoundingMode.HALF_UP);
    }

    protected static DecimalExp cast(Exp<?> exp) {

        if (exp instanceof DecimalExp) {
            return (DecimalExp) exp;
        }

        // TODO: a map of casting converters

        Class<?> t = exp.getType();
        if (t.equals(BigDecimal.class)) {
            Exp<BigDecimal> bdExp = (Exp<BigDecimal>) exp;
            return DecimalExp1.mapVal("castAsDecimal", bdExp, Function.identity());
        }

        if (t.equals(BigInteger.class)) {
            Exp<BigInteger> biExp = (Exp<BigInteger>) exp;
            return DecimalExp1.mapVal("castAsDecimal", biExp, BigDecimal::new);
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<Number> nExp = (Exp<Number>) exp;
            return DecimalExp1.mapVal("castAsDecimal", nExp, n -> new BigDecimal(n.doubleValue()));
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return DecimalExp1.mapVal("castAsDecimal", sExp, BigDecimal::new);
        }

        throw new IllegalArgumentException("Expression type '" + t.getName() + "' can't be converted to Double");
    }

    @Override
    public DecimalExp add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return DecimalExp2.mapVal("+",
                cast(left),
                cast(right),
                BigDecimal::add);
    }

    @Override
    public DecimalExp sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return DecimalExp2.mapVal("-",
                cast(left),
                cast(right),
                BigDecimal::subtract);
    }

    @Override
    public DecimalExp mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return DecimalExp2.mapVal("*",
                cast(left),
                cast(right),
                BigDecimal::multiply);
    }

    @Override
    public DecimalExp div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return DecimalExp2.mapVal("/",
                cast(left),
                cast(right),
                (BigDecimal n1, BigDecimal n2) ->
                        // TODO: would be nice to be able to specify the result scale explicitly instead of first
                        //  inflating the scale, then trimming trailing zeros. It will not be as slow, and will not
                        //  overflow
                        n1.divide(n2, divisionContext(n1, n2)).stripTrailingZeros());
    }

    @Override
    public DecimalExp mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return DecimalExp2.mapVal("%",
                cast(left),
                cast(right),
                (BigDecimal n1, BigDecimal n2) ->

                        // TODO: would be nice to be able to specify the result scale explicitly instead of first
                        //  inflating the scale, then trimming trailing zeros. It will not be as slow, and will not
                        //  overflow

                        // TODO: result sign handling in a manner compatible with "%"
                        n1.remainder(n2, divisionContext(n1, n2)).stripTrailingZeros());
    }

    @Override
    public DecimalExp abs(Exp<? extends Number> exp) {
        return DecimalExp1.mapVal("abs", cast(exp), BigDecimal::abs);
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return cast(exp);
    }

    @Override
    public DecimalExp cumSum(Exp<? extends Number> exp) {
        return DecimalExp1.map("cumSum", cast(exp), DecimalAggregators::cumSum);
    }

    @Deprecated
    @Override
    public DecimalExp sum(Exp<? extends Number> exp) {
        return sum(exp, null);
    }

    @Override
    public DecimalExp sum(Exp<? extends Number> exp, Condition filter) {
        return new DecimalReduceExp1<>("sum", cast(exp), DecimalAggregators::sum, filter);
    }

    @Deprecated
    @Override
    public DecimalExp min(Exp<? extends Number> exp) {
        return min(exp, null);
    }

    @Override
    public DecimalExp min(Exp<? extends Number> exp, Condition filter) {
        return new DecimalReduceExp1<>("min", cast(exp), ComparableAggregators::min, filter);
    }

    @Deprecated
    @Override
    public DecimalExp max(Exp<? extends Number> exp) {
        return max(exp, null);
    }

    @Override
    public DecimalExp max(Exp<? extends Number> exp, Condition filter) {
        return new DecimalReduceExp1<>("max", cast(exp), ComparableAggregators::max, filter);
    }

    @Deprecated
    @Override
    public DecimalExp avg(Exp<? extends Number> exp) {
        return avg(exp, null);
    }

    @Override
    public DecimalExp avg(Exp<? extends Number> exp, Condition filter) {
        // TODO
        throw new UnsupportedOperationException("TODO: support for BigDecimal.avg");
    }

    @Deprecated
    @Override
    public DecimalExp median(Exp<? extends Number> exp) {
        return median(exp, null);
    }

    @Override
    public DecimalExp median(Exp<? extends Number> exp, Condition filter) {
        return new DecimalReduceExp1<>("median", cast(exp), s -> Percentiles.ofDecimals(s, 0.5), filter);
    }

    @Override
    public DecimalExp quantile(Exp<? extends Number> exp, double q, Condition filter) {
        return new DecimalReduceExp1<>("quantile", cast(exp), s -> Percentiles.ofDecimals(s, q), filter);
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        // TODO: should we apply ".stripTrailingZeros()" for consistency, but at the expense of performance?
        return MapCondition2.mapVal("=", cast(left), cast(right), BigDecimal::equals);
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
