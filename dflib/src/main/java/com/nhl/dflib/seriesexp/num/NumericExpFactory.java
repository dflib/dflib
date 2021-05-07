package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.SeriesExp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.11
 */
public abstract class NumericExpFactory {

    protected static final Map<Class<? extends Number>, Integer> typeConversionRank;
    protected static final Map<Class<? extends Number>, NumericExpFactory> factories;

    static {
        typeConversionRank = new HashMap<>();

        typeConversionRank.put(BigDecimal.class, 0);

        typeConversionRank.put(Double.class, 1);
        typeConversionRank.put(Double.TYPE, 1);

        typeConversionRank.put(Float.class, 2);
        typeConversionRank.put(Float.TYPE, 2);

        typeConversionRank.put(Long.class, 3);
        typeConversionRank.put(Long.TYPE, 3);

        typeConversionRank.put(Integer.class, 4);
        typeConversionRank.put(Integer.TYPE, 4);

        // we don't have factories for these yet
        // typeConversionRank.put(Short.class, 5);
        // typeConversionRank.put(Byte.class, 6);

        factories = new HashMap<>();

        factories.put(BigDecimal.class, new DecimalExpFactory());

        factories.put(Double.class, new DoubleExpFactory());
        factories.put(Double.TYPE, factories.get(Double.class));

        factories.put(Integer.class, new IntExpFactory());
        factories.put(Integer.TYPE, factories.get(Integer.class));

        factories.put(Long.class, new LongExpFactory());
        factories.put(Long.TYPE, factories.get(Long.class));
    }

    public abstract NumericSeriesExp<?> add(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract NumericSeriesExp<?> subtract(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract NumericSeriesExp<?> multiply(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract NumericSeriesExp<?> divide(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract NumericSeriesExp<?> mod(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract NumericSeriesExp<?> sum(SeriesExp<? extends Number> exp);

    public abstract NumericSeriesExp<?> min(SeriesExp<? extends Number> exp);

    public abstract NumericSeriesExp<?> max(SeriesExp<? extends Number> exp);

    public abstract NumericSeriesExp<?> avg(SeriesExp<? extends Number> exp);

    public abstract NumericSeriesExp<?> median(SeriesExp<? extends Number> exp);

    public abstract NumericSeriesExp<BigDecimal> castAsDecimal(NumericSeriesExp<?> exp, int scale);

    public abstract SeriesCondition lt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract SeriesCondition le(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract SeriesCondition gt(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public abstract SeriesCondition ge(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right);

    public static NumericExpFactory factory(SeriesExp<? extends Number> exp) {
        return factory(exp.getType());
    }

    public static NumericExpFactory factory(Class<? extends Number> type) {

        NumericExpFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported arithmetic type: " + type);
        }

        return factory;
    }

    public static NumericExpFactory factory(SeriesExp<? extends Number> left, SeriesExp<? extends Number> right) {
        return factory(left.getType(), right.getType());
    }

    public static NumericExpFactory factory(Class<? extends Number> left, Class<? extends Number> right) {

        Class<? extends Number> type = factoryType(left, right);

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
}
