package org.dflib.ql.antlr4;

import org.antlr.v4.runtime.Token;
import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.exp.bool.BoolScalarExp;
import org.dflib.exp.flow.IfNullExp;
import org.dflib.exp.str.StrScalarExp;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExpParserUtils {

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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Exp<T> val(T value) {
        Class type = value != null ? value.getClass() : Object.class;
        return val(value, type);
    }

    @SuppressWarnings("unchecked")
    public static <T, V extends T> Exp<T> val(V value, Class<T> type) {
        if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            return (Exp<T>) Exp.$intVal((Integer) value);
        } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            return (Exp<T>) Exp.$longVal((Long) value);
        } else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
            return (Exp<T>) Exp.$floatVal((Float) value);
        } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
            return (Exp<T>) Exp.$doubleVal((Double) value);
        } else if (BigInteger.class.equals(type)) {
            return (Exp<T>) Exp.$bigintVal((BigInteger) value);
        } else if (BigDecimal.class.equals(type)) {
            return (Exp<T>) Exp.$decimalVal((BigDecimal) value);
        } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            return (Exp<T>) Exp.$boolVal((Boolean) value);
        } else if (String.class.equals(type)) {
            return (Exp<T>) Exp.$strVal((String) value);
        } else if (LocalTime.class.equals(type)) {
            return (Exp<T>) Exp.$timeVal((LocalTime) value);
        } else if (LocalDate.class.equals(type)) {
            return (Exp<T>) Exp.$dateVal((LocalDate) value);
        } else if (LocalDateTime.class.equals(type)) {
            return (Exp<T>) Exp.$dateTimeVal((LocalDateTime) value);
        } else if (OffsetDateTime.class.equals(type)) {
            return (Exp<T>) Exp.$offsetDateTimeVal((OffsetDateTime) value);
        } else {
            return Exp.$val(value);
        }
    }

    public static NumExp<Integer> intCol(Object columnId) {
        return col(columnId, Exp::$int, Exp::$int);
    }

    public static NumExp<Long> longCol(Object columnId) {
        return col(columnId, Exp::$long, Exp::$long);
    }

    public static NumExp<Float> floatCol(Object columnId) {
        return col(columnId, Exp::$float, Exp::$float);
    }

    public static NumExp<Double> doubleCol(Object columnId) {
        return col(columnId, Exp::$double, Exp::$double);
    }

    public static NumExp<BigInteger> bigintCol(Object columnId) {
        return col(columnId, Exp::$bigint, Exp::$bigint);
    }

    public static DecimalExp decimalCol(Object columnId) {
        return col(columnId, Exp::$decimal, Exp::$decimal);
    }

    public static StrExp strCol(Object columnId) {
        return col(columnId, Exp::$str, Exp::$str);
    }

    public static Condition boolCol(Object columnId) {
        return col(columnId, Exp::$bool, Exp::$bool);
    }

    public static DateExp dateCol(Object columnId) {
        return col(columnId, Exp::$date, Exp::$date);
    }

    public static TimeExp timeCol(Object columnId) {
        return col(columnId, Exp::$time, Exp::$time);
    }

    public static DateTimeExp dateTimeCol(Object columnId) {
        return col(columnId, Exp::$dateTime, Exp::$dateTime);
    }

    public static OffsetDateTimeExp offsetCol(Object columnId) {
        return col(columnId, Exp::$offsetDateTime, Exp::$offsetDateTime);
    }

    public static Exp<?> col(Object columnId) {
        return col(columnId, Exp::$col, Exp::$col);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Exp<?> ifNullExp(Exp a, Exp b) {
        return new IfNullExp<>(a, b);
    }

    public static NumExp<?> addOrSub(NumExp a, NumExp b, Token op) {
        switch (op.getType()) {
            case ExpParser.ADD:
                return a.add(b);
            case ExpParser.SUB:
                return a.sub(b);
            default:
                throw new RuntimeException("Unknown operator: " + op.getText());
        }
    }

    public static NumExp<?> mulDivOrMod(NumExp a, NumExp b, Token op) {
        switch (op.getType()) {
            case ExpParser.MUL:
                return a.mul(b);
            case ExpParser.DIV:
                return a.div(b);
            case ExpParser.MOD:
                return a.mod(b);
            default:
                throw new RuntimeException("Unknown operator: " + op.getText());
        }
    }

    public static Number parseIntegerValue(String token) {
        int radix = radix(token);
        String sanitizedToken = sanitizeNumScalar(token, radix);
        Matcher matcher = Pattern.compile("(?<number>.+?)[ilh]?").matcher(sanitizedToken);
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
        String normalizedToken = token.replaceAll("_+", "").toLowerCase();
        Matcher matcher = Pattern.compile("(?<number>.+?)[fdm]?").matcher(normalizedToken);
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
        String normalizedToken = token.replaceAll("_+", "").toLowerCase();
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

    public static LocalDate parseDateValue(String token) {
        return LocalDate.parse(token, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalTime parseTimeValue(String token) {
        return LocalTime.parse(token, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static LocalDateTime parseDateTimeValue(String token) {
        return LocalDateTime.parse(token, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static OffsetDateTime parseOffsetDateTimeValue(String token) {
        return OffsetDateTime.parse(token, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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

    private static <T> T col(Object columnId, Function<Integer, T> byIndex, Function<String, T> byName) {
        if (columnId instanceof Integer) {
            return byIndex.apply((Integer) columnId);
        } else if (columnId instanceof String) {
            return byName.apply((String) columnId);
        } else {
            throw new IllegalArgumentException("An integer or a string expected");
        }
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
        return sanitizeNumScalar(token, radix, null);
    }

    private static String sanitizeNumScalar(String token, int radix, String postfix) {
        String scalar = token.toLowerCase();
        scalar = scalar.replaceAll("_+", "");
        scalar = postfix != null ? scalar.replaceAll(postfix.toLowerCase() + "$", "") : scalar;
        switch (radix) {
            case 2:
                return scalar.replaceFirst("0b", "");
            case 8:
                return scalar.replaceFirst("0(?=.)", "");
            case 16:
                return scalar.replaceFirst("0x", "");
            default:
                return scalar;
        }
    }

    public static String unescapeIdentifier(String raw) {
        if (raw == null) {
            return null;
        }

        return raw.replaceAll("``", "`");
    }

    public static String unescapeString(String raw) {
        if (raw == null) {
            return null;
        }

        return raw.replaceAll("''", "'");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Exp<?> array(Exp exp, String type) {
        Object[] array;
        try {
            array = (Object[]) Array.newInstance(Class.forName(type), 0);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return exp.array(array);
    }

    @SuppressWarnings("unchecked")
    static <T extends Exp<?>> T param(PositionalParamSource source, BiConsumer<Object, Exp<?>> validator) {
        Object next = source.next();
        Exp<?> exp = val(next);
        validator.accept(next, exp);
        return (T) exp;
    }

    public static NumExp<?> numParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof NumExp)) {
                throw new RuntimeException("Invalid value for a numeric parameter: " + o);
            }
        });
    }

    public static StrExp strParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof StrScalarExp)) {
                throw new RuntimeException("Invalid value for a string parameter: " + o);
            }
        });
    }

    public static Condition boolParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof BoolScalarExp)) {
                throw new RuntimeException("Invalid value for a boolean parameter: " + o);
            }
        });
    }

    public static DateExp dateParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof DateExp)) {
                throw new RuntimeException("Invalid value for a date parameter: " + o);
            }
        });
    }

    public static TimeExp timeParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof TimeExp)) {
                throw new RuntimeException("Invalid value for a time parameter: " + o);
            }
        });
    }

    public static DateTimeExp dateTimeParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof DateTimeExp)) {
                throw new RuntimeException("Invalid value for a datetime parameter: " + o);
            }
        });
    }

    public static OffsetDateTimeExp offsetDateTimeParam(PositionalParamSource source) {
        return param(source, (o, e) -> {
            if(!(e instanceof OffsetDateTimeExp)) {
                throw new RuntimeException("Invalid value for a offset datetime parameter: " + o);
            }
        });
    }

    public static Object[] objArrayParam(PositionalParamSource source) {
        Object next = source.next();
        if(next instanceof Collection) {
            return ((Collection<?>) next).toArray();
        } else if(next instanceof Object[]) {
            return (Object[]) next;
        }
        throw new RuntimeException("Expected array or collection parameter");
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] arrayParam(PositionalParamSource source, IntFunction<T[]> arrayGenerator) {
        Object next = source.next();
        if(next instanceof Collection) {
            return ((Collection<?>) next).toArray(arrayGenerator);
        } else if(next instanceof Object[]) {
            Object[] data = (Object[]) next;
            T[] result = arrayGenerator.apply(data.length);
            for(int i=0; i<data.length; i++) {
                result[i] = (T)data[i]; // unsafe cast here
            }
        }
        throw new RuntimeException("Expected array or collection parameter");
    }

    public static Number[] numArrayParam(PositionalParamSource source) {
        return arrayParam(source, Number[]::new);
    }

    public static String[] strArrayParam(PositionalParamSource source) {
        return arrayParam(source, String[]::new);
    }
}
