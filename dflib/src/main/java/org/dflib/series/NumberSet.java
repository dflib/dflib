package org.dflib.series;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts the elements of an "in" / "notIn" list to the value type of a primitive Series. An element matches a
 * Series value when the two are equal after widening to the wider of their types, the same as with the "eq"
 * expression. So an element of a wider type is taken only if the Series type can represent it exactly, and an
 * element of a narrower type is always taken. Elements that are not numbers, or cannot be represented, are skipped,
 * as they cannot match a value of the Series.
 */
final class NumberSet {

    private NumberSet() {
    }

    static Set<Integer> ints(Object[] values) {
        Set<Integer> set = new HashSet<>();
        if (values != null) {
            for (Object o : values) {
                if (o instanceof Number n) {
                    Long l = exactLong(n);
                    if (l != null && l.intValue() == l) {
                        set.add(l.intValue());
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
                if (o instanceof BigDecimal bd) {
                    Double d = exactDouble(bd);
                    if (d != null) {
                        set.add(d);
                    }
                } else if (o instanceof Number n) {
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
                    Float f = exactFloat(n);
                    if (f != null) {
                        set.add(f);
                    }
                }
            }
        }
        return set;
    }

    /**
     * Returns the value as a float. A double or a decimal is wider than a float, so null is returned when the
     * conversion is not exact.
     */
    private static Float exactFloat(Number n) {
        return switch (n) {
            case Float f -> f;
            case Double d -> (double) d.floatValue() == d ? Float.valueOf(d.floatValue()) : null;
            case BigDecimal bd -> {
                Double d = exactDouble(bd);
                yield d != null && (double) d.floatValue() == d ? Float.valueOf(d.floatValue()) : null;
            }
            default -> n.floatValue();
        };
    }

    private static Double exactDouble(BigDecimal bd) {
        double d = bd.doubleValue();

        // the same double to decimal conversion as the one used to compare the two in expressions
        return Double.isFinite(d) && BigDecimal.valueOf(d).compareTo(bd) == 0 ? d : null;
    }

    /**
     * Returns the value as a long when the conversion is exact, or null when the value is fractional, out of the
     * long range, or NaN.
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
            default -> longOfDouble(n.doubleValue());
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
