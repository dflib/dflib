package org.dflib.map;

/**
 * @since 2.0.0
 */
public class NumToPrimitiveMappers {

    public static int toInt(Number n) {
        if (n == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive int");
        }

        return n.intValue();
    }

    public static int toInt(Number n, int forNull) {
        return n != null ? n.intValue() : forNull;
    }

    public static long toLong(Number n) {
        if (n == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive long");
        }

        return n.longValue();
    }

    public static long toLong(Number n, long forNull) {
        return n != null ? n.longValue() : forNull;
    }

    public static float toFloat(Number n) {
        if (n == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive float");
        }

        return n.floatValue();
    }

    public static float toFloat(Number n, float forNull) {
        return n != null ? n.floatValue() : forNull;
    }

    public static double toDouble(Number n) {
        if (n == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive double");
        }

        return n.floatValue();
    }

    public static double toDouble(Number n, double forNull) {
        return n != null ? n.floatValue() : forNull;
    }
}
