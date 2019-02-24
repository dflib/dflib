package com.nhl.dflib.jdbc.connector;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BindingValueToStringConverter {

    private static final int TRIM_VALUES_THRESHOLD = 30;

    // hex encoding outputs 2 chars for each byte
    private static final int BYTE_TRIM_VALUES_THRESHOLD = TRIM_VALUES_THRESHOLD / 2;


    private Map<Class<?>, Function<Object, String>> converters;
    private Function<Object, String> nullConverter;
    private Function<Object, String> defaultConverter;

    public BindingValueToStringConverter() {
        this.nullConverter = o -> "null";
        this.defaultConverter = o -> o.getClass().getName() + "@" + System.identityHashCode(o);

        this.converters = new ConcurrentHashMap<>();

        converters.put(String.class, o -> {

            StringBuilder buffer = new StringBuilder();
            buffer.append('\'');

            // lets escape quotes
            String literal = (String) o;
            if (literal.length() > TRIM_VALUES_THRESHOLD) {
                literal = literal.substring(0, TRIM_VALUES_THRESHOLD) + "...";
            }

            int curPos = 0;
            int endPos;

            while ((endPos = literal.indexOf('\'', curPos)) >= 0) {
                buffer.append(literal.substring(curPos, endPos + 1)).append('\'');
                curPos = endPos + 1;
            }

            if (curPos < literal.length())
                buffer.append(literal.substring(curPos));

            buffer.append('\'');
            return buffer.toString();
        });

        converters.put(Number.class, Object::toString);
        converters.put(Boolean.class, Object::toString);
        converters.put(byte[].class, this::convertByteArray);
        converters.put(Timestamp.class, this::convertTimestamp);
        converters.put(Date.class, this::convertSqlDate);
        converters.put(Time.class, this::convertTime);

        // TODO: add more types...
    }

    protected Function<Object, String> converter(Object value) {
        if (value == null) {
            return nullConverter;
        }

        Class<?> type = value.getClass();
        return converters.computeIfAbsent(type, t -> {
            Function<Object, String> c = null;
            Class<?> st = t.getSuperclass();

            while (c == null && st != Object.class) {
                c = converters.get(st);
                st = st.getSuperclass();
            }

            return c != null ? c : defaultConverter;
        });
    }

    public String convert(Object value) {
        return converter(value).apply(value);
    }

    private String convertTimestamp(Object value) {
        if (value == null) {
            return nullConverter.apply(value);
        }

        Timestamp ts = (Timestamp) value;
        return ts.toLocalDateTime().toString();
    }

    private String convertSqlDate(Object value) {
        if (value == null) {
            return nullConverter.apply(value);
        }

        Date d = (Date) value;
        return d.toLocalDate().toString();
    }

    private String convertTime(Object value) {
        if (value == null) {
            return nullConverter.apply(value);
        }

        Time t = (Time) value;
        return t.toLocalTime().toString();
    }

    private String convertByteArray(Object value) {
        if (value == null) {
            return nullConverter.apply(value);
        }

        final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        byte[] bytes = (byte[]) value;

        int l = bytes.length <= BYTE_TRIM_VALUES_THRESHOLD ? bytes.length : BYTE_TRIM_VALUES_THRESHOLD;

        char[] out = new char[l << 1];
        int i = 0;

        for (int var4 = 0; i < l; ++i) {
            out[var4++] = DIGITS[(240 & bytes[i]) >>> 4];
            out[var4++] = DIGITS[15 & bytes[i]];
        }

        String bytesAsString = new String(out);
        return bytes.length <= BYTE_TRIM_VALUES_THRESHOLD ? bytesAsString : bytesAsString + "...";
    }
}
