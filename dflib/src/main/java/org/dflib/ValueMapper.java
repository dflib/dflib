package org.dflib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@FunctionalInterface
public interface ValueMapper<V, VR> {

    static ValueMapper<String, Boolean> stringToBool() {
        return s -> s != null ? Boolean.parseBoolean(s) : null;
    }
    
    static ValueMapper<String, Integer> stringToInt() {
        return s -> s != null && s.length() > 0 ? Integer.valueOf(s) : null;
    }
    
    static ValueMapper<String, String> stringTrim() {
        return s -> {
            if (s == null) {
                return s;
            }

            s = s.trim();
            return s.isEmpty() ? null : s;
        };
    }

    static ValueMapper<String, String> stringToString() {
        return s -> s;
    }

    /**
     * @see Exp#castAsLong()
     */
    static ValueMapper<String, Long> stringToLong() {
        return s -> s != null && s.length() > 0 ? Long.valueOf(s) : null;
    }
    
    static ValueMapper<String, Double> stringToDouble() {
        return s -> s != null && s.length() > 0 ? Double.valueOf(s) : null;
    }

    static ValueMapper<String, Float> stringToFloat() {
        return s -> s != null && s.length() > 0 ? Float.valueOf(s) : null;
    }

    static ValueMapper<String, BigDecimal> stringToBigDecimal() {
        return s -> s != null && s.length() > 0 ? new BigDecimal(s) : null;
    }

    static ValueMapper<String, BigInteger> stringToBigInteger() {
        return s -> s != null && s.length() > 0 ? new BigInteger(s) : null;
    }

    static ValueMapper<String, LocalDate> stringToDate() {
        return s -> s != null && s.length() > 0 ? LocalDate.parse(s) : null;
    }

    static ValueMapper<String, LocalDate> stringToDate(DateTimeFormatter formatter) {
        return s -> s != null && s.length() > 0 ? LocalDate.parse(s, formatter) : null;
    }

    static ValueMapper<String, LocalTime> stringToTime() {
        return s -> s != null && s.length() > 0 ? LocalTime.parse(s) : null;
    }

    static ValueMapper<String, LocalTime> stringToTime(DateTimeFormatter formatter) {
        return s -> s != null && s.length() > 0 ? LocalTime.parse(s, formatter) : null;
    }

    static ValueMapper<String, LocalDateTime> stringToDateTime() {
        return s -> s != null && s.length() > 0 ? LocalDateTime.parse(s) : null;
    }

    static ValueMapper<String, LocalDateTime> stringToDateTime(DateTimeFormatter formatter) {
        return s -> s != null && s.length() > 0 ? LocalDateTime.parse(s, formatter) : null;
    }

    static <E extends Enum<E>> ValueMapper<String, E> stringToEnum(Class<E> type) {
        return s -> s != null && s.length() > 0 ? Enum.valueOf(type, s) : null;
    }

    static <E extends Enum<E>> ValueMapper<? extends Number, E> numToEnum(Class<E> type) {
        E[] allValues = type.getEnumConstants();
        return n -> n != null ? allValues[n.intValue()] : null;
    }


    default <VR1> ValueMapper<V, VR1> and(ValueMapper<VR, VR1> after) {
        return v -> after.map(this.map(v));
    }

    VR map(V v);
}
