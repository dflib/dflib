package com.nhl.dflib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@FunctionalInterface
public interface ValueMapper<V, VR> {

    /**
     * @see Exp#castAsCondition()
     * @since 0.16
     */
    static ValueMapper<String, Boolean> stringToBool() {
        return s -> s != null ? Boolean.parseBoolean(s) : null;
    }

    /**
     * @see Exp#castAsInt()
     */
    static ValueMapper<String, Integer> stringToInt() {
        return s -> s != null && s.length() > 0 ? Integer.valueOf(s) : null;
    }

    /**
     * @see StrExp#trim()
     * @since 0.7
     */
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

    /**
     * @see Exp#castAsDouble()
     */
    static ValueMapper<String, Double> stringToDouble() {
        return s -> s != null && s.length() > 0 ? Double.valueOf(s) : null;
    }

    /**
     * @since 0.6
     */
    static ValueMapper<String, Float> stringToFloat() {
        return s -> s != null && s.length() > 0 ? Float.valueOf(s) : null;
    }

    /**
     * @see Exp#castAsDecimal()
     * @since 0.6
     */
    static ValueMapper<String, BigDecimal> stringToBigDecimal() {
        return s -> s != null && s.length() > 0 ? new BigDecimal(s) : null;
    }

    /**
     * @since 0.6
     */
    static ValueMapper<String, BigInteger> stringToBigInteger() {
        return s -> s != null && s.length() > 0 ? new BigInteger(s) : null;
    }

    /**
     * @see Exp#castAsDate()
     */
    static ValueMapper<String, LocalDate> stringToDate() {
        return s -> s != null && s.length() > 0 ? LocalDate.parse(s) : null;
    }

    /**
     * @see Exp#castAsDate(DateTimeFormatter)
     */
    static ValueMapper<String, LocalDate> stringToDate(DateTimeFormatter formatter) {
        return s -> s != null && s.length() > 0 ? LocalDate.parse(s, formatter) : null;
    }

    /**
     * @since 0.16
     */
    static ValueMapper<String, LocalTime> stringToTime() {
        return s -> s != null && s.length() > 0 ? LocalTime.parse(s) : null;
    }

    /**
     * @since 0.16
     */
    static ValueMapper<String, LocalTime> stringToTime(DateTimeFormatter formatter) {
        return s -> s != null && s.length() > 0 ? LocalTime.parse(s, formatter) : null;
    }

    static ValueMapper<String, LocalDateTime> stringToDateTime() {
        return s -> s != null && s.length() > 0 ? LocalDateTime.parse(s) : null;
    }

    static ValueMapper<String, LocalDateTime> stringToDateTime(DateTimeFormatter formatter) {
        return s -> s != null && s.length() > 0 ? LocalDateTime.parse(s, formatter) : null;
    }

    /**
     * @see StrExp#castAsEnum(Class)
     * @since 0.6
     */
    static <E extends Enum<E>> ValueMapper<String, E> stringToEnum(Class<E> type) {
        return s -> s != null && s.length() > 0 ? Enum.valueOf(type, s) : null;
    }

    /**
     * @see NumExp#castAsEnum(Class)
     * @since 0.6
     */
    static <E extends Enum<E>> ValueMapper<? extends Number, E> numToEnum(Class<E> type) {
        E[] allValues = type.getEnumConstants();
        return n -> n != null ? allValues[n.intValue()] : null;
    }

    /**
     * @since 0.7
     */
    default <VR1> ValueMapper<V, VR1> and(ValueMapper<VR, VR1> after) {
        return v -> after.map(this.map(v));
    }

    VR map(V v);
}
