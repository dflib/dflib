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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

class ExpParserUtils {

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

    static int parseIntegerValue(String token) {
        int radix = radix(token);
        return Integer.valueOf(sanitizeNumScalar(token, radix), radix);
    }

    static long parseLongValue(String token) {
        int radix = radix(token);
        return Long.valueOf(sanitizeNumScalar(token, radix, "l"), radix);
    }

    static Number parseFloatingPointValue(String token) {
        String scalar = token.toLowerCase();
        scalar = scalar.replaceAll("_+", "");
        if (scalar.endsWith("f")) {
            return Float.valueOf(scalar);
        } else {
            return Double.valueOf(scalar);
        }
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
        int dotPosition = token.indexOf(".");
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
