package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.BoolValueMapper;
import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Extractor;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.jdbc.connector.JdbcFunction;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * @param <T>
 * @since 0.16
 */
@FunctionalInterface
public interface JdbcExtractorFactory<T> {

    static Extractor<ResultSet, Boolean> $bool(int pos) {
        BoolValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getBoolean(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return Extractor.$bool(mapper);
    }

    static Extractor<ResultSet, Integer> $int(int pos) {
        IntValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getInt(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return Extractor.$int(mapper);
    }

    static Extractor<ResultSet, Long> $long(int pos) {
        LongValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getLong(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return Extractor.$long(mapper);
    }

    static Extractor<ResultSet, Double> $double(int pos) {
        DoubleValueMapper<ResultSet> mapper = rs -> {
            try {
                return rs.getDouble(pos);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return Extractor.$double(mapper);
    }

    static Extractor<ResultSet, Object> $col(int pos) {
        return fromJdbcFunction(rs -> rs.getObject(pos));
    }

    static Extractor<ResultSet, LocalDate> $date(int pos) {
        return fromJdbcFunction(rs -> {
            Date date = rs.getDate(pos);
            return date != null ? date.toLocalDate() : null;
        });
    }

    static Extractor<ResultSet, LocalTime> $time(int pos) {
        return fromJdbcFunction(rs -> {
            Time time = rs.getTime(pos, Calendar.getInstance());
            return time != null ? time.toLocalTime() : null;
        });
    }

    static Extractor<ResultSet, LocalDateTime> $datetime(int pos) {
        return fromJdbcFunction(rs -> {
            Timestamp timestamp = rs.getTimestamp(pos, Calendar.getInstance());
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        });
    }

    static <T> Extractor<ResultSet, T> fromJdbcFunction(JdbcFunction<ResultSet, T> f) {

        ValueMapper<ResultSet, T> mapper = rs -> {
            try {
                return f.apply(rs);
            } catch (SQLException e) {
                throw new RuntimeException("Error performing SQL operation", e);
            }
        };

        return Extractor.$col(mapper);
    }

    Extractor<ResultSet, T> createExtractor(int pos);
}
