package org.dflib.exp.parser.antlr4;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.exp.flow.IfNullExp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

class ExpParserUtils {

    private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <T> Exp<T> val(T value) {
        Class type = value != null ? value.getClass() : Object.class;
        return val(value, type);
    }

    @SuppressWarnings("unchecked")
    static <T, V extends T> Exp<T> val(V value, Class<T> type) {
        if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            return (Exp<T>) Exp.$intVal((Integer) value);
        } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            return (Exp<T>) Exp.$longVal((Long) value);
        } else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
            return (Exp<T>) Exp.$floatVal((Float) value);
        } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
            return (Exp<T>) Exp.$doubleVal((Double) value);
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

    static NumExp<Integer> intCol(Object columnId) {
        return col(columnId, Exp::$int, Exp::$int);
    }

    static NumExp<Long> longCol(Object columnId) {
        return col(columnId, Exp::$long, Exp::$long);
    }

    static NumExp<Float> floatCol(Object columnId) {
        return col(columnId, Exp::$float, Exp::$float);
    }

    static NumExp<Double> doubleCol(Object columnId) {
        return col(columnId, Exp::$double, Exp::$double);
    }

    static DecimalExp decimalCol(Object columnId) {
        return col(columnId, Exp::$decimal, Exp::$decimal);
    }

    static StrExp strCol(Object columnId) {
        return col(columnId, Exp::$str, Exp::$str);
    }

    static Condition boolCol(Object columnId) {
        return col(columnId, Exp::$bool, Exp::$bool);
    }

    static DateExp dateCol(Object columnId) {
        return col(columnId, Exp::$date, Exp::$date);
    }

    static TimeExp timeCol(Object columnId) {
        return col(columnId, Exp::$time, Exp::$time);
    }

    static DateTimeExp dateTimeCol(Object columnId) {
        return col(columnId, Exp::$dateTime, Exp::$dateTime);
    }

    static OffsetDateTimeExp offsetCol(Object columnId) {
        return col(columnId, Exp::$offsetDateTime, Exp::$offsetDateTime);
    }

    static Exp<?> col(Object columnId) {
        return col(columnId, Exp::$col, Exp::$col);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static Exp<?> ifNullExp(Exp a, Exp b) {
        return new IfNullExp<>(a, b);
    }

    static Number parseIntegerValue(String token) {
        int radix = radix(token);
        String sanitizedToken = sanitizeNumScalar(token, radix);
        BigInteger value = new BigInteger(sanitizedToken, radix);
        if (value.compareTo(INT_MIN) >= 0 && value.compareTo(INT_MAX) <= 0) {
            return value.intValue();
        }
        if (value.compareTo(LONG_MIN) >= 0 && value.compareTo(LONG_MAX) <= 0) {
            return value.longValue();
        }
        return value;
    }

    static Number parseFloatingPointValue(String token) {
        String scalar = token.toLowerCase();
        scalar = scalar.replaceAll("_+", "");
        BigDecimal value = new BigDecimal(scalar);

        float floatValue = value.floatValue();
        if (Float.isFinite(floatValue) && new BigDecimal(floatValue).compareTo(value) == 0) {
            return floatValue;
        }
        double doubleValue = value.doubleValue();
        if (Double.isFinite(doubleValue) && new BigDecimal(doubleValue).compareTo(value) == 0) {
            return doubleValue;
        }
        return value;
    }

    static LocalDate parseDateValue(String token) {
        return LocalDate.parse(token, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    static LocalTime parseTimeValue(String token) {
        return LocalTime.parse(token, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    static LocalDateTime parseDateTimeValue(String token) {
        return LocalDateTime.parse(token, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    static OffsetDateTime parseOffsetDateTimeValue(String token) {
        return OffsetDateTime.parse(token, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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
        if (!token.contains(".") && lowerToken.startsWith("0", offset) && token.length() > offset + 1) {
            return 8;
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

    static String unescapeString(String raw) {
        if (raw == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char currentChar = raw.charAt(i);
            if (currentChar != '\\' || i + 1 >= raw.length()) {
                result.append(currentChar);
                continue;
            }

            char nextChar = raw.charAt(i + 1);
            if (nextChar == 'u' && i + 5 < raw.length()) {
                String hex = raw.substring(i + 2, i + 6);
                try {
                    int unicodeValue = Integer.parseInt(hex, 16);
                    result.append((char) unicodeValue);
                    i += 5;
                    continue;
                } catch (NumberFormatException e) {
                    result.append("\\u");
                    i++;
                    continue;
                }
            }

            if (nextChar == '"' || nextChar == '\'') {
                result.append(nextChar);
            } else {
                result.append(currentChar);
                result.append(nextChar);
            }
            i++;
        }
        return result.toString();
    }
}
