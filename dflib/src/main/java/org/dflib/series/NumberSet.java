package org.dflib.series;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts the elements of an "in" / "notIn" list to the value type of a primitive Series. Integer types take the
 * elements whose values they can represent exactly, floating-point types take any number. Elements that are not
 * numbers, or do not fit the type, are skipped, as they can not match a value of the Series.
 */
final class NumberSet {

    private NumberSet() {
    }

    static Set<Integer> ints(Object[] values) {
        Set<Integer> set = new HashSet<>();
        if (values != null) {
            for (Object o : values) {
                if (o instanceof Integer i) {
                    set.add(i);
                } else if (o instanceof Number n) {
                    Long l = exactLong(n);
                    if (l != null && (int) (long) l == l) {
                        set.add((int) (long) l);
                    }
                }
            }
        }
        return set;
    }

    static Set<Long> longs(Object[] values) {
        Set<Long> set = new HashSet<>();
        if (values != null) {
            for (Object o : values) {
                if (o instanceof Number n) {
                    Long l = exactLong(n);
                    if (l != null) {
                        set.add(l);
                    }
                }
            }
        }
        return set;
    }

    static Set<Double> doubles(Object[] values) {
        Set<Double> set = new HashSet<>();
        if (values != null) {
            for (Object o : values) {
                if (o instanceof Number n) {
                    set.add(n.doubleValue());
                }
            }
        }
        return set;
    }

    static Set<Float> floats(Object[] values) {
        Set<Float> set = new HashSet<>();
        if (values != null) {
            for (Object o : values) {
                if (o instanceof Number n) {
                    set.add(n.floatValue());
                }
            }
        }
        return set;
    }

    /**
     * Returns the value as a long when the conversion is exact, or null when the value is fractional, out of the
     * long range, or not a number.
     */
    private static Long exactLong(Number n) {
        return switch (n) {
            case Long l -> l;
            case Integer i -> i.longValue();
            case Short s -> s.longValue();
            case Byte b -> b.longValue();
            case BigInteger bi -> bi.bitLength() < 64 ? bi.longValue() : null;
            case BigDecimal bd -> longOfDecimal(bd);
            case Float f -> longOfDouble(f.doubleValue());
            case Double d -> longOfDouble(d);
            default -> longOfDecimal(BigDecimal.valueOf(n.doubleValue()));
        };
    }

    private static Long longOfDouble(double d) {
        // "(long) d" saturates and swallows NaN, so check the range and integrality first; the upper bound is
        // exclusive, as 2^63 is itself a double but not a long
        return d == Math.rint(d) && d >= -0x1p63 && d < 0x1p63
                ? (long) d
                : null;
    }

    private static Long longOfDecimal(BigDecimal bd) {
        try {
            return bd.longValueExact();
        } catch (ArithmeticException e) {
            return null;
        }
    }
}
