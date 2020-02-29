package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.loader.BooleanSeriesBuilder;
import com.nhl.dflib.jdbc.connector.loader.DoubleSeriesBuilder;
import com.nhl.dflib.jdbc.connector.loader.IntSeriesBuilder;
import com.nhl.dflib.jdbc.connector.loader.LongSeriesBuilder;
import com.nhl.dflib.jdbc.connector.loader.ObjectSeriesBuilder;
import com.nhl.dflib.jdbc.connector.loader.SeriesBuilder;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @param <T>
 * @since 0.6
 */
@FunctionalInterface
public interface SeriesBuilderFactory<T> {

    static SeriesBuilder<ResultSet, Boolean> booleanAccum(int pos) {
        return new BooleanSeriesBuilder<>(rs -> {
            try {
                return rs.getBoolean(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        });
    }

    static SeriesBuilder<ResultSet, Integer> intAccum(int pos) {
        return new IntSeriesBuilder<>(rs -> {
            try {
                return rs.getInt(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        });
    }

    static SeriesBuilder<ResultSet, Long> longAccum(int pos) {
        return new LongSeriesBuilder<>(rs -> {
            try {
                return rs.getLong(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        });
    }

    static SeriesBuilder<ResultSet, Double> doubleAccum(int pos) {
        return new DoubleSeriesBuilder<>(rs -> {
            try {
                return rs.getDouble(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        });
    }

    static SeriesBuilder<ResultSet, Object> objectAccum(int pos) {
        return fromJdbcFunction(rs -> rs.getObject(pos));
    }

    static SeriesBuilder<ResultSet, LocalDate> dateAccum(int pos) {
        return fromJdbcFunction(rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate() : null;
        });
    }

    static SeriesBuilder<ResultSet, LocalTime> timeAccum(int pos) {
        return fromJdbcFunction(rs -> {
            Time time = rs.getTime(pos);
            return time != null ? time.toLocalTime() : null;
        });
    }

    static SeriesBuilder<ResultSet, LocalDateTime> timestampAccum(int pos) {
        return fromJdbcFunction(rs -> {
            Timestamp timestamp = rs.getTimestamp(pos);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        });
    }

    static <T> SeriesBuilder<ResultSet, T> fromJdbcFunction(JdbcFunction<ResultSet, T> f) {
        return new ObjectSeriesBuilder<>(rs -> {
            try {
                return f.apply(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        });
    }

    SeriesBuilder<ResultSet, T> createAccum(int pos);
}
