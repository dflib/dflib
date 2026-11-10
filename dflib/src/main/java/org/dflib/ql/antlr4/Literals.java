package org.dflib.ql.antlr4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts the text of the QL literal tokens to Java values.
 */
class Literals {

    private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigDecimal FLOAT_MIN = BigDecimal.valueOf(Float.MIN_NORMAL);
    private static final BigDecimal FLOAT_MAX = BigDecimal.valueOf(Float.MAX_VALUE);
    private static final BigDecimal DOUBLE_MIN = BigDecimal.valueOf(Double.MIN_NORMAL);
    private static final BigDecimal DOUBLE_MAX = BigDecimal.valueOf(Double.MAX_VALUE);

    private static final int FLOAT_SIGNIFICAND = 24;
    private static final int FLOAT_MIN_EXPONENT = -37;
    private static final int FLOAT_MAX_EXPONENT = 37;
    private static final int DOUBLE_SIGNIFICAND = 53;
    private static final int DOUBLE_MIN_EXPONENT = -307;
    private static final int DOUBLE_MAX_EXPONENT = 307;

    private static final Pattern INTEGER_LITERAL = Pattern.compile("(?<number>.+?)[ilh]?");
    private static final Pattern FLOAT_LITERAL = Pattern.compile("(?<number>.+?)[fdm]?");
    private static final Pattern UNDERSCORES = Pattern.compile("_+");
    private static final Pattern OCTAL_PREFIX = Pattern.compile("0(?=.)");

    public static Number parseIntegerValue(String token) {
        int radix = radix(token);
        String sanitizedToken = sanitizeNumScalar(token, radix);
        Matcher matcher = INTEGER_LITERAL.matcher(sanitizedToken);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid integer literal: " + token);
        }

        String number = matcher.group("number");
        if (sanitizedToken.endsWith("i")) {
            return Integer.valueOf(number, radix);
        }
        if (sanitizedToken.endsWith("l")) {
            return Long.valueOf(number, radix);
        }

        BigInteger value = new BigInteger(number, radix);
        if (sanitizedToken.endsWith("h")) {
            return value;
        }

        if (value.compareTo(INT_MIN) >= 0 && value.compareTo(INT_MAX) <= 0) {
            return value.intValue();
        }
        if (value.compareTo(LONG_MIN) >= 0 && value.compareTo(LONG_MAX) <= 0) {
            return value.longValue();
        }
        return value;
    }

    public static Number parseFloatingPointValue(String token) {
        String normalizedToken = stripUnderscores(token).toLowerCase();
        Matcher matcher = FLOAT_LITERAL.matcher(normalizedToken);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid floating point literal: " + token);
        }

        String number = matcher.group("number");
        if (normalizedToken.endsWith("f")) {
            return Float.valueOf(number);
        }
        if (normalizedToken.endsWith("d")) {
            return Double.valueOf(number);
        }

        BigDecimal value = parseDecimalValue(number);
        value = value.stripTrailingZeros();
        if (normalizedToken.endsWith("m")) {
            return value;
        }

        value = value.round(new MathContext(FLOAT_SIGNIFICAND));

        if (mayFitFloat(value)) {
            return value.floatValue();
        }
        if (mayFitDouble(value)) {
            return value.doubleValue();
        }
        return value;
    }

    public static BigDecimal parseDecimalValue(String token) {
        if (token == null) {
            throw new NullPointerException("Input string cannot be null.");
        }
        String normalizedToken = stripUnderscores(token).toLowerCase();
        boolean isNegative = normalizedToken.startsWith("-");
        boolean hasSign = isNegative || normalizedToken.startsWith("+");
        int startIndex = hasSign ? 1 : 0;

        if (!normalizedToken.startsWith("0x", startIndex)) {
            return new BigDecimal(normalizedToken);
        }

        // Extract the main parts of the hex string
        int pIndex = token.indexOf('p', startIndex);
        if (pIndex == -1) {
            throw new NumberFormatException("Input must contain 'p' or 'P' for exponent.");
        }
        String mantissaPart = token.substring(startIndex + 2, pIndex);
        String exponentPart = token.substring(pIndex + 1);

        // Validate and parse the exponent
        int exponent;
        try {
            exponent = Integer.parseInt(exponentPart);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid exponent: " + exponentPart);
        }

        // Handling mantissa
        int hexPointIndex = mantissaPart.indexOf('.');
        BigInteger integerMantissa;
        int fractionalBits = 0;
        if (hexPointIndex >= 0) {
            String integerPart = mantissaPart.substring(0, hexPointIndex);
            String fractionalPart = mantissaPart.substring(hexPointIndex + 1);
            fractionalBits = fractionalPart.length() * 4;
            integerMantissa = new BigInteger(integerPart + fractionalPart, 16);
        } else {
            integerMantissa = new BigInteger(mantissaPart, 16);
        }

        int binaryExponent = exponent - fractionalBits;
        BigDecimal result = new BigDecimal(integerMantissa);
        if (binaryExponent != 0) {
            BigDecimal factor = BigDecimal.valueOf(2).pow(Math.abs(binaryExponent));
            result = binaryExponent > 0 ? result.multiply(factor) : result.divide(factor, MathContext.DECIMAL128);
        }

        return isNegative ? result.negate() : result;
    }

    private static boolean mayFitFloat(BigDecimal value) {
        int significand = value.precision();
        int exponent = value.precision() - value.scale() - 1;
        if (significand > FLOAT_SIGNIFICAND || exponent < FLOAT_MIN_EXPONENT || exponent > FLOAT_MAX_EXPONENT) {
            return false;
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        BigDecimal abs = value.abs();
        return abs.compareTo(FLOAT_MIN) >= 0 && abs.compareTo(FLOAT_MAX) <= 0;
    }

    private static boolean mayFitDouble(BigDecimal value) {
        int significand = value.precision();
        int exponent = value.precision() - value.scale() - 1;
        if (significand > DOUBLE_SIGNIFICAND || exponent < DOUBLE_MIN_EXPONENT || exponent > DOUBLE_MAX_EXPONENT) {
            return false;
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        BigDecimal abs = value.abs();
        return abs.compareTo(DOUBLE_MIN) >= 0 && abs.compareTo(DOUBLE_MAX) <= 0;
    }

    private static int radix(String token) {
        int offset = token.startsWith("+") || token.startsWith("-") ? 1 : 0;
        String lowerToken = token.toLowerCase();
        if (lowerToken.startsWith("0x", offset)) {
            return 16;
        }
        if (lowerToken.startsWith("0b", offset)) {
            return 2;
        }
        return 10;
    }

    private static String sanitizeNumScalar(String token, int radix) {
        String scalar = stripUnderscores(token.toLowerCase());
        return switch (radix) {
            case 2 -> stripPrefix(scalar, "0b");
            case 8 -> OCTAL_PREFIX.matcher(scalar).replaceFirst("");
            case 16 -> stripPrefix(scalar, "0x");
            default -> scalar;
        };
    }

    private static String stripUnderscores(String token) {
        // most literals have no underscores, so skip the regex for those
        return token.indexOf('_') < 0 ? token : UNDERSCORES.matcher(token).replaceAll("");
    }

    /**
     * Removes the first occurrence of a literal prefix, the same as {@code replaceFirst} would for a regex-free
     * pattern.
     */
    private static String stripPrefix(String s, String prefix) {
        int i = s.indexOf(prefix);
        return i < 0 ? s : s.substring(0, i) + s.substring(i + prefix.length());
    }

    public static String unescapeIdentifier(String raw) {
        return raw != null ? raw.replace("``", "`") : null;
    }

    public static String unescapeString(String raw) {
        return raw != null ? raw.replace("''", "'") : null;
    }
}
