package com.nhl.dflib.jdbc.connector;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface ValueReaderFactory<T> {

    static JdbcFunction<ResultSet, Object> objectReader(int pos) {
        return rs -> rs.getObject(pos);
    }

    static JdbcFunction<ResultSet, LocalDate> dateReader(int pos) {
        return rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate() : null;
        };
    }

    static JdbcFunction<ResultSet, LocalTime> timeReader(int pos) {
        return rs -> {
            Time time = rs.getTime(pos);
            return time != null ? time.toLocalTime() : null;
        };
    }

    static JdbcFunction<ResultSet, LocalDateTime> timestampReader(int pos) {
        return rs -> {
            Timestamp timestamp = rs.getTimestamp(pos);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        };
    }

    JdbcFunction<ResultSet, T> reader(int pos);
}
