package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

/**
 * Fast-path parser for double values. Delegates to {@link Double#parseDouble)} method for complex cases.
 */
final class DoubleParser {

    private static final long MAX_EXACT_MANTISSA = 1L << 53;
    private static final long MAX_LONG_VALUE = Long.MAX_VALUE / 10;
    private static final long MAX_LONG_REMAINDER = Long.MAX_VALUE % 10;
    private static final double[] POW10 = {
            1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9,
            1e10, 1e11, 1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18
    };

    private DoubleParser() {
    }

    static double parse(DataSlice slice) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();

        if (from >= to) {
            throw new NumberFormatException("Empty input");
        }

        if (FloatingPointParser.matchNaN(data, from, to)) {
            return Double.NaN;
        }

        if (FloatingPointParser.matchInfinity(data, from, to)) {
            return data[from] == '-' ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }

        boolean minus = false;
        if (data[from] == '-') {
            minus = true;
            from++;
        }

        if (from >= to) {
            throw new NumberFormatException("No digits found");
        }

        long mantissa = 0;
        int scale = 0;
        boolean seenDot = false;
        boolean seenDigit = false;

        while (from < to) {
            char c = data[from];
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                seenDigit = true;

                if (checkLongOverflow(mantissa, digit)) {
                    return fallback(slice);
                }
                mantissa = mantissa * 10 + digit;

                if (seenDot) {
                    scale++;
                    if (scale >= POW10.length) {
                        return fallback(slice);
                    }
                }

            } else if (c == '.' && !seenDot) {
                seenDot = true;
            } else if (c == 'e' || c == 'E') {
                return fallback(slice);
            } else {
                throw new NumberFormatException("Invalid character: '" + c + "'");
            }
            from++;
        }

        if (!seenDigit) {
            throw new NumberFormatException("No digits found");
        }

        if (mantissa >= MAX_EXACT_MANTISSA) {
            return fallback(slice);
        }

        double result = mantissa / POW10[scale];
        return minus ? -result : result;
    }

    private static double fallback(DataSlice slice) {
        return Double.parseDouble(slice.toString());
    }

    private static boolean checkLongOverflow(long value, int digit) {
        return value > MAX_LONG_VALUE || (value == MAX_LONG_VALUE && digit > MAX_LONG_REMAINDER);
    }
}
