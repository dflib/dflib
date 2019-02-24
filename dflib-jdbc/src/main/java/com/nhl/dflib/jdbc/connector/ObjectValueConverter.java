package com.nhl.dflib.jdbc.connector;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Converts java types into proper sql types.
 */
public class ObjectValueConverter {

    private Map<Class<?>, Function<Object, Object>> converters;
    private Function<Object, Object> nullConverter;
    private Function<Object, Object> defaultConverter;

    public ObjectValueConverter() {

        this.converters = new ConcurrentHashMap<>();

        this.nullConverter = o -> null;
        this.defaultConverter = o -> o;

        converters.put(LocalDate.class, o -> Date.valueOf((LocalDate) o));
        converters.put(LocalTime.class, o -> Time.valueOf((LocalTime) o));
        converters.put(LocalDateTime.class, o -> Timestamp.valueOf((LocalDateTime) o));
    }

    protected Function<Object, Object> converter(Object value) {
        if (value == null) {
            return nullConverter;
        }

        Class<?> type = value.getClass();
        return converters.computeIfAbsent(type, t -> {
            Function<Object, Object> c = null;
            Class<?> st = t.getSuperclass();

            while (c == null && st != Object.class) {
                c = converters.get(st);
                st = st.getSuperclass();
            }

            return c != null ? c : defaultConverter;
        });
    }

    /**
     * Converts {@link LocalDate}, {@link LocalTime} and {@link LocalDateTime} into proper {@link java.sql.Types},
     * otherwise returns {@code value} itself
     *
     * @param value an object to be converted
     * @return converted {@code value}
     */
    public Object convert(Object value) {
        return converter(value).apply(value);
    }
}
