package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.*;
import com.nhl.dflib.jdbc.connector.JdbcFunction;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * @param <T>
 * @since 0.8
 */
@FunctionalInterface
public interface ColumnBuilderFactory<T> {

    static ColumnBuilder<Boolean> booleanAccum(int pos) {
        BooleanValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getBoolean(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new ColumnBuilder<>(new BooleanConverter<>(mapper), new BooleanAccumulator());
    }

    static ColumnBuilder<Integer> intAccum(int pos) {
        IntValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getInt(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new ColumnBuilder<>(new IntConverter<>(mapper), new IntAccumulator());
    }

    static ColumnBuilder<Long> longAccum(int pos) {
        LongValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getLong(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new ColumnBuilder<>(new LongConverter<>(mapper), new LongAccumulator());
    }

    static ColumnBuilder<Double> doubleAccum(int pos) {
        DoubleValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getDouble(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new ColumnBuilder<>(new DoubleConverter<>(mapper), new DoubleAccumulator());
    }

    static ColumnBuilder<Object> objectAccum(int pos) {
        return fromJdbcFunction(rs -> rs.getObject(pos));
    }

    static ColumnBuilder<LocalDate> dateAccum(int pos) {
        return fromJdbcFunction(rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate() : null;
        });
    }

    static ColumnBuilder<LocalTime> timeAccum(int pos) {
        return fromJdbcFunction(rs -> {
            Time time = rs.getTime(pos, Calendar.getInstance());
            return time != null ? time.toLocalTime() : null;
        });
    }

    static ColumnBuilder<LocalDateTime> timestampAccum(int pos) {
        return fromJdbcFunction(rs -> {
            Timestamp timestamp = rs.getTimestamp(pos, Calendar.getInstance());
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        });
    }

    static <T> ColumnBuilder<T> fromJdbcFunction(JdbcFunction<ResultSet, T> f) {

        ValueMapper<ResultSet, T> mapper = rs -> {
            try {
                return f.apply(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new ColumnBuilder<>(new ObjectConverter<>(mapper), new ObjectAccumulator<>());
    }

    ColumnBuilder<T> createAccum(int pos);
}
