package org.dflib.map;

/**
 * @since 2.0.0
 */
public class StringToPrimitiveMappers {

    public static boolean toBool(String s) {
        // null-safe... "parseBoolean" returns false for null
        return Boolean.parseBoolean(s);
    }

    public static int toInt(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive int");
        }

        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive int");
        }

        return Integer.parseInt(s);
    }

    public static int toInt(String s, int forNull) {
        return s != null && !s.isEmpty() ? Integer.parseInt(s) : forNull;
    }

    public static long toLong(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive long");
        }

        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive long");
        }

        return Long.parseLong(s);
    }

    public static long toLong(String s, long forNull) {
        return s != null && !s.isEmpty() ? Long.parseLong(s) : forNull;
    }

    public static float toFloat(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive float");
        }

        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive float");
        }

        return Float.parseFloat(s);
    }

    public static float toFloat(String s, float forNull) {
        return s != null && !s.isEmpty() ? Float.parseFloat(s) : forNull;
    }

    public static double toDouble(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive double");
        }

        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive double");
        }

        return Double.parseDouble(s);
    }

    public static double toDouble(String s, double forNull) {
        return s != null && !s.isEmpty() ? Double.parseDouble(s) : forNull;
    }
}
