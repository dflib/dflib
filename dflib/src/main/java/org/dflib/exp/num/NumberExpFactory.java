package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;

import java.math.BigInteger;

class NumberExpFactory extends NumericExpFactory {

    @Override
    public NumExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberExp2("+", left, right, NumericExpFactory::add);
    }

    @Override
    public NumExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberExp2("-", left, right, NumericExpFactory::sub);
    }

    @Override
    public NumExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberExp2("*", left, right, NumericExpFactory::mul);
    }

    @Override
    public NumExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberExp2("/", left, right, NumericExpFactory::div);
    }

    @Override
    public NumExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberExp2("%", left, right, NumericExpFactory::mod);
    }

    @Override
    public NumExp<?> abs(Exp<? extends Number> exp) {
        return new NumberExp1("abs", exp, NumericExpFactory::abs);
    }

    @Override
    public NumExp<?> sqrt(Exp<? extends Number> exp) {
        return new NumberExp1("sqrt", exp, NumericExpFactory::sqrt);
    }

    @Override
    public NumExp<?> negate(Exp<? extends Number> exp) {
        return new NumberNegateExp(exp);
    }

    @Override
    public NumExp<Integer> castAsInt(NumExp<?> exp) {
        return IntExp1.map("castAsInt", cast(exp), s -> NumberTypeResolver.convert(s, RANK_INT));
    }

    @Override
    public NumExp<Long> castAsLong(NumExp<?> exp) {
        return LongExp1.map("castAsLong", cast(exp), s -> NumberTypeResolver.convert(s, RANK_LONG));
    }

    @Override
    public NumExp<Double> castAsDouble(NumExp<?> exp) {
        return DoubleExp1.map("castAsDouble", cast(exp), s -> NumberTypeResolver.convert(s, RANK_DOUBLE));
    }

    @Override
    public NumExp<Float> castAsFloat(NumExp<?> exp) {
        return FloatExp1.map("castAsFloat", cast(exp), s -> NumberTypeResolver.convert(s, RANK_FLOAT));
    }

    @Override
    public NumExp<BigInteger> castAsBigint(NumExp<?> exp) {
        return BigintExp1.map("castAsBigint", cast(exp), s -> NumberTypeResolver.convert(s, RANK_BIG_INTEGER));
    }

    @Override
    public DecimalExp castAsDecimal(NumExp<?> exp) {
        return DecimalExp1.map("castAsDecimal", cast(exp), s -> NumberTypeResolver.convert(s, RANK_BIG_DECIMAL));
    }

    @Override
    public NumExp<?> cumSum(Exp<? extends Number> exp) {
        return new NumberExp1("cumSum", exp, NumericExpFactory::cumSum);
    }

    @Override
    public NumExp<?> sum(Exp<? extends Number> exp, Condition filter) {
        return new NumberReduceExp1("sum", exp, (factory, resolved) -> factory.sum(resolved, null), filter);
    }

    @Override
    public NumExp<?> min(Exp<? extends Number> exp, Condition filter) {
        return new NumberReduceExp1("min", exp, (factory, resolved) -> factory.min(resolved, null), filter);
    }

    @Override
    public NumExp<?> max(Exp<? extends Number> exp, Condition filter) {
        return new NumberReduceExp1("max", exp, (factory, resolved) -> factory.max(resolved, null), filter);
    }

    @Override
    public NumExp<?> avg(Exp<? extends Number> exp, Condition filter) {
        return new NumberReduceExp1("avg", exp, (factory, resolved) -> factory.avg(resolved, null), filter);
    }

    @Override
    public NumExp<?> median(Exp<? extends Number> exp, Condition filter) {
        return new NumberReduceExp1("median", exp, (factory, resolved) -> factory.median(resolved, null), filter);
    }

    @Override
    public NumExp<?> quantile(Exp<? extends Number> exp, double q, Condition filter) {
        return new NumberReduceExp1("quantile", exp, (factory, resolved) -> factory.quantile(resolved, q, null), filter);
    }

    @Override
    public NumExp<?> round(Exp<? extends Number> exp) {
        return new NumberExp1("round", exp, NumericExpFactory::round);
    }

    @Override
    public NumExp<?> variance(Exp<? extends Number> exp, boolean usePopulationVariance) {
        return new NumberReduceExp1(
                "variance",
                exp,
                (factory, resolved) -> factory.variance(resolved, usePopulationVariance),
                null
        );
    }

    @Override
    public NumExp<?> stdDev(Exp<? extends Number> exp, boolean usePopulationStdDev) {
        return new NumberReduceExp1(
                "stdDev",
                exp,
                (factory, resolved) -> factory.stdDev(resolved, usePopulationStdDev),
                null
        );
    }

    @Override
    public Condition eq(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberCondition2("=", left, right, NumericExpFactory::eq);
    }

    @Override
    public Condition ne(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberCondition2("!=", left, right, NumericExpFactory::ne);
    }

    @Override
    public Condition lt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberCondition2("<", left, right, NumericExpFactory::lt);
    }

    @Override
    public Condition le(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberCondition2("<=", left, right, NumericExpFactory::le);
    }

    @Override
    public Condition gt(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberCondition2(">", left, right, NumericExpFactory::gt);
    }

    @Override
    public Condition ge(Exp<? extends Number> left, Exp<? extends Number> right) {
        return new NumberCondition2(">=", left, right, NumericExpFactory::ge);
    }

    @Override
    public Condition between(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return new NumberCondition3("between", "and", left, from, to, NumericExpFactory::between);
    }

    @Override
    public Condition notBetween(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to) {
        return new NumberCondition3("notBetween", "and", left, from, to, NumericExpFactory::notBetween);
    }

    @SuppressWarnings("unchecked")
    private Exp<Number> cast(Exp<?> exp) {
        return (Exp<Number>) exp;
    }
}
