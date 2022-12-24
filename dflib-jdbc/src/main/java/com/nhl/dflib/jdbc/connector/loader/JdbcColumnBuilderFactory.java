package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.*;
import com.nhl.dflib.jdbc.connector.JdbcFunction;
import com.nhl.dflib.builder.BooleanExtractor;
import com.nhl.dflib.builder.DoubleExtractor;
import com.nhl.dflib.builder.IntExtractor;
import com.nhl.dflib.builder.LongExtractor;
import com.nhl.dflib.builder.ObjectExtractor;

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

    static JdbcSeriesBuilder<Boolean> booleanCol(int pos) {
        BooleanValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getBoolean(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcSeriesBuilder<>(new BooleanExtractor<>(mapper));
    }

    static JdbcSeriesBuilder<Integer> intCol(int pos) {
        IntValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getInt(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcSeriesBuilder<>(new IntExtractor<>(mapper));
    }

    static JdbcSeriesBuilder<Long> longCol(int pos) {
        LongValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getLong(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcSeriesBuilder<>(new LongExtractor<>(mapper));
    }

    static JdbcSeriesBuilder<Double> doubleCol(int pos) {
        DoubleValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getDouble(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcSeriesBuilder<>(new DoubleExtractor<>(mapper));
    }

    static JdbcSeriesBuilder<Object> objectCol(int pos) {
        return fromJdbcFunction(rs -> rs.getObject(pos));
    }

    static JdbcSeriesBuilder<LocalDate> dateCol(int pos) {
        return fromJdbcFunction(rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate() : null;
        });
    }

    static JdbcSeriesBuilder<LocalTime> timeCol(int pos) {
        return fromJdbcFunction(rs -> {
            Time time = rs.getTime(pos, Calendar.getInstance());
            return time != null ? time.toLocalTime() : null;
        });
    }

    static JdbcSeriesBuilder<LocalDateTime> timestampCol(int pos) {
        return fromJdbcFunction(rs -> {
            Timestamp timestamp = rs.getTimestamp(pos, Calendar.getInstance());
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        });
    }

    static <T> JdbcSeriesBuilder<T> fromJdbcFunction(JdbcFunction<ResultSet, T> f) {

        ValueMapper<ResultSet, T> mapper = rs -> {
            try {
                return f.apply(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return new JdbcSeriesBuilder<>(new ObjectExtractor<>(mapper));
    }

    JdbcSeriesBuilder<T> createBuilder(int pos);
}
