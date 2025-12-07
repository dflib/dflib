package org.dflib.map;

public class AnyToPrimitiveMappers {

    public static boolean toBool(Object o) {
        if (o instanceof Boolean b) {
            return b;
        }

        if (o instanceof Number n) {
            return n.intValue() != 0;
        }

        String s = o != null ? o.toString() : null;

        // null-safe... "parseBoolean" returns false for null
        return Boolean.parseBoolean(s);
    }

    public static int toInt(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive int");
        }

        if (o instanceof Number) {
            return ((Number) o).intValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1 : 0;
        }

        String s = o.toString();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive int");
        }

        return Integer.parseInt(s);
    }

    public static int toInt(Object o, int forNull) {
        if (o == null) {
            return forNull;
        }

        if (o instanceof Number) {
            return ((Number) o).intValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1 : 0;
        }

        String s = o.toString();
        return s.isEmpty() ? forNull : Integer.parseInt(s);
    }


    public static long toLong(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive long");
        }

        if (o instanceof Number) {
            return ((Number) o).longValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1L : 0L;
        }

        String s = o.toString();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive long");
        }

        return Long.parseLong(s);
    }

    public static long toLong(Object o, long forNull) {
        if (o == null) {
            return forNull;
        }

        if (o instanceof Number) {
            return ((Number) o).longValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1L : 0L;
        }

        String s = o.toString();
        return s.isEmpty() ? forNull : Long.parseLong(s);
    }

    public static float toFloat(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive float");
        }

        if (o instanceof Number) {
            return ((Number) o).floatValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1.f : 0.f;
        }

        String s = o.toString();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive float");
        }

        return Float.parseFloat(s);
    }

    public static float toFloat(Object o, float forNull) {
        if (o == null) {
            return forNull;
        }

        if (o instanceof Number) {
            return ((Number) o).floatValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1.f : 0.f;
        }

        String s = o.toString();
        return s.isEmpty() ? forNull : Float.parseFloat(s);
    }

    public static double toDouble(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Can't convert a null to a primitive double");
        }

        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1.d : 0.d;
        }

        String s = o.toString();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Can't convert an empty String to a primitive double");
        }

        return Double.parseDouble(s);
    }

    public static double toDouble(Object o, double forNull) {
        if (o == null) {
            return forNull;
        }

        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }

        if (o instanceof Boolean) {
            return ((Boolean) o) ? 1d : 0d;
        }

        String s = o.toString();
        return s.isEmpty() ? forNull : Double.parseDouble(s);
    }
}
