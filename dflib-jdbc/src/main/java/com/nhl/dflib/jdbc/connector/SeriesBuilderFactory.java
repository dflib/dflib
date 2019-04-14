package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.builder.IntSeriesBuilder;
import com.nhl.dflib.builder.MappedSeriesBuilder;
import com.nhl.dflib.builder.SeriesBuilder;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@FunctionalInterface
public interface SeriesBuilderFactory<T> {

    static SeriesBuilder<ResultSet, Integer> intAccum(int pos) {
        return new IntSeriesBuilder<>(rs -> {
            try {
                return rs.getInt(pos);
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
        return new MappedSeriesBuilder<>(rs -> {
            try {
                return f.apply(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        });
    }

    SeriesBuilder<ResultSet, T> createAccum(int pos);
}
