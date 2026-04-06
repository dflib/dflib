package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.map.MapExp1;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstraction over various numeric types allowing to mix and match different types in a given operation.
 */
public abstract class NumericExpFactory {

    static final int RANK_NUMBER = 0;
    static final int RANK_BIG_DECIMAL = 1;
    static final int RANK_DOUBLE = 2;
    static final int RANK_FLOAT = 3;
    static final int RANK_BIG_INTEGER = 4;
    static final int RANK_LONG = 5;
    static final int RANK_INT = 6;

    protected static final Map<Class<? extends Number>, Integer> typeConversionRank;
    protected static final Map<Class<? extends Number>, NumericExpFactory> factories;
    protected static final DecimalExpFactory decimalFactory;

    static {

        decimalFactory = new DecimalExpFactory();

        typeConversionRank = new HashMap<>();

        typeConversionRank.put(Number.class, RANK_NUMBER);
        typeConversionRank.put(BigDecimal.class, RANK_BIG_DECIMAL);

        typeConversionRank.put(Double.class, RANK_DOUBLE);
        typeConversionRank.put(Double.TYPE, RANK_DOUBLE);

        typeConversionRank.put(Float.class, RANK_FLOAT);
        typeConversionRank.put(Float.TYPE, RANK_FLOAT);

        typeConversionRank.put(BigInteger.class, RANK_BIG_INTEGER);

        typeConversionRank.put(Long.class, RANK_LONG);
        typeConversionRank.put(Long.TYPE, RANK_LONG);

        typeConversionRank.put(Integer.class, RANK_INT);
        typeConversionRank.put(Integer.TYPE, RANK_INT);

        // we don't have factories for these yet
        // typeConversionRank.put(Short.class, 5);
        // typeConversionRank.put(Byte.class, 6);

        factories = new HashMap<>();

        factories.put(Number.class, new NumberExpFactory());

        factories.put(BigDecimal.class, decimalFactory);
        factories.put(BigInteger.class, new BigintExpFactory());

        factories.put(Float.class, new FloatExpFactory());
        factories.put(Float.TYPE, factories.get(Float.class));

        factories.put(Double.class, new DoubleExpFactory());
        factories.put(Double.TYPE, factories.get(Double.class));

        factories.put(Integer.class, new IntExpFactory());
        factories.put(Integer.TYPE, factories.get(Integer.class));

        factories.put(Long.class, new LongExpFactory());
        factories.put(Long.TYPE, factories.get(Long.class));
    }

    /**
     * Provides direct access to the DecimalExpFactory, that can be used to return {@link DecimalExp} instead of
     * {@link NumExp}.
     */
    // note that using individual factories directly, bypassing "NumericExpFactory.factory(type)" method is going to
    // break widening conversions. It is only possible for DecimalExpFactory, that has precedence over every other
    // factory
    public static DecimalExpFactory decimalFactory() {
        return decimalFactory;
    }

    public static NumericExpFactory factory(Exp<? extends Number> exp) {
        return factory(exp.getType());
    }

    public static NumericExpFactory factory(Class<? extends Number> type) {

        NumericExpFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported arithmetic type: " + type);
        }

        return factory;
    }

    public static NumericExpFactory factory(Exp<? extends Number> left, Exp<? extends Number> right) {
        return factory(left.getType(), right.getType());
    }

    /**
     * @since 2.0.0
     */
    public static NumericExpFactory factory(Exp<? extends Number> one, Exp<? extends Number> two, Exp<? extends Number> three) {
        return factory(one.getType(), two.getType(), three.getType());
    }

    public static NumericExpFactory factory(Class<? extends Number> left, Class<? extends Number> right) {

        Class<? extends Number> type = factoryType(left, right);

        NumericExpFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported arithmetic type: " + type);
        }

        return factory;
    }


    public static NumericExpFactory factory(Class<? extends Number> one, Class<? extends Number> two, Class<? extends Number> three) {

        Class<? extends Number> type = factoryType(factoryType(one, two), three);

        NumericExpFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported arithmetic type: " + type);
        }

        return factory;
    }

    protected static Class<? extends Number> factoryType(Class<? extends Number> left, Class<? extends Number> right) {

        Integer lr = typeConversionRank.get(left);
        if (lr == null) {
            throw new IllegalArgumentException("Unsupported numeric type: " + left);
        }

        Integer rr = typeConversionRank.get(right);
        if (rr == null) {
            throw new IllegalArgumentException("Unsupported numeric type: " + right);
        }

        // widening conversion that matches standard Java primitive arithmetics
        return lr.compareTo(rr) < 0 ? left : right;
    }

    public abstract NumExp<?> add(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract NumExp<?> sub(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract NumExp<?> mul(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract NumExp<?> div(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract NumExp<?> mod(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract NumExp<?> abs(Exp<? extends Number> exp);

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> sqrt(Exp<? extends Number> exp);

    public abstract NumExp<?> negate(Exp<? extends Number> exp);


    public abstract NumExp<?> cumSum(Exp<? extends Number> exp);

    /**
     * @deprecated in favor of {@link #sum(Exp, Condition)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public NumExp<?> sum(Exp<? extends Number> exp) {
        return sum(exp, null);
    }

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> sum(Exp<? extends Number> exp, Condition filter);

    /**
     * @deprecated in favor of {@link #min(Exp, Condition)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public NumExp<?> min(Exp<? extends Number> exp) {
        return min(exp, null);
    }

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> min(Exp<? extends Number> exp, Condition filter);

    /**
     * @deprecated in favor of {@link #max(Exp, Condition)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public NumExp<?> max(Exp<? extends Number> exp) {
        return max(exp, null);
    }

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> max(Exp<? extends Number> exp, Condition filter);

    /**
     * @deprecated in favor of {@link #avg(Exp, Condition)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public NumExp<?> avg(Exp<? extends Number> exp) {
        return avg(exp, null);
    }

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> avg(Exp<? extends Number> exp, Condition filter);

    /**
     * @deprecated in favor of {@link #median(Exp, Condition)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public NumExp<?> median(Exp<? extends Number> exp) {
        return median(exp, null);
    }

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> median(Exp<? extends Number> exp, Condition filter);

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> quantile(Exp<? extends Number> exp, double q, Condition filter);

    /**
     * @since 2.0.0
     */
    public abstract NumExp<?> round(Exp<? extends Number> exp);

    /**
     * @since 2.0.0
     */
    public <N extends Number> NumExp<N> shift(Exp<N> exp, int offset, N filler) {
        return new NumShiftExp<>(exp, offset, filler);
    }

    /**
     * @since 1.3.0
     */
    public abstract NumExp<?> variance(Exp<? extends Number> exp, boolean usePopulationVariance);

    /**
     * @since 1.3.0
     */
    public abstract NumExp<?> stdDev(Exp<? extends Number> exp, boolean usePopulationStdDev);

    public NumExp<Integer> castAsInt(NumExp<?> exp) {
        return IntExp1.mapVal("castAsInt", exp, Number::intValue);
    }

    public NumExp<Long> castAsLong(NumExp<?> exp) {
        return LongExp1.mapVal("castAsLong", exp, Number::longValue);
    }

    public NumExp<Double> castAsDouble(NumExp<?> exp) {
        return DoubleExp1.mapVal("castAsDouble", exp, Number::doubleValue);
    }

    /**
     * @since 1.1.x
     */
    public NumExp<Float> castAsFloat(NumExp<?> exp) {
        return FloatExp1.mapVal("castAsFloat", exp, Number::floatValue);
    }

    /**
     * @since 2.0.0
     */
    public NumExp<BigInteger> castAsBigint(NumExp<?> exp) {
        return BigintExp1.mapVal("castAsBigint", exp, n -> BigInteger.valueOf(n.longValue()));
    }

    public abstract DecimalExp castAsDecimal(NumExp<?> exp);


    public <E extends Enum<E>> Exp<E> castAsEnum(NumExp<?> exp, Class<E> type) {
        E[] allValues = type.getEnumConstants();
        // TODO: ugly generics stripping. Any better way to design this?
        NumExp noGenericExp = exp;
        return MapExp1.mapVal("castAsEnum", type, noGenericExp, (Number i) -> allValues[i.intValue()]);
    }

    public abstract Condition eq(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract Condition ne(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract Condition lt(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract Condition le(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract Condition gt(Exp<? extends Number> left, Exp<? extends Number> right);

    public abstract Condition ge(Exp<? extends Number> left, Exp<? extends Number> right);


    public abstract Condition between(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to);

    /**
     * @since 2.0.0
     */
    public abstract Condition notBetween(Exp<? extends Number> left, Exp<? extends Number> from, Exp<? extends Number> to);

}
