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
public interface JdbcColumnBuilderFactory<T> {

    static JdbcColumnBuilder<Boolean> booleanCol(int pos) {
        BooleanValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getBoolean(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcColumnBuilder<>(new BooleanConverter<>(mapper), new BooleanAccumulator());
    }

    static JdbcColumnBuilder<Integer> intCol(int pos) {
        IntValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getInt(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcColumnBuilder<>(new IntConverter<>(mapper), new IntAccumulator());
    }

    static JdbcColumnBuilder<Long> longCol(int pos) {
        LongValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getLong(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcColumnBuilder<>(new LongConverter<>(mapper), new LongAccumulator());
    }

    static JdbcColumnBuilder<Double> doubleCol(int pos) {
        DoubleValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getDouble(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcColumnBuilder<>(new DoubleConverter<>(mapper), new DoubleAccumulator());
    }

    static JdbcColumnBuilder<Object> objectCol(int pos) {
        return fromJdbcFunction(rs -> rs.getObject(pos));
    }

    static JdbcColumnBuilder<LocalDate> dateCol(int pos) {
        return fromJdbcFunction(rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate() : null;
        });
    }

    static JdbcColumnBuilder<LocalTime> timeCol(int pos) {
        return fromJdbcFunction(rs -> {
            Time time = rs.getTime(pos, Calendar.getInstance());
            return time != null ? time.toLocalTime() : null;
        });
    }

    static JdbcColumnBuilder<LocalDateTime> timestampCol(int pos) {
        return fromJdbcFunction(rs -> {
            Timestamp timestamp = rs.getTimestamp(pos, Calendar.getInstance());
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        });
    }

    static <T> JdbcColumnBuilder<T> fromJdbcFunction(JdbcFunction<ResultSet, T> f) {

        ValueMapper<ResultSet, T> mapper = rs -> {
            try {
                return f.apply(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcColumnBuilder<>(new ObjectConverter<>(mapper), new ObjectAccumulator<>());
    }

    JdbcColumnBuilder<T> createBuilder(int pos);
}
