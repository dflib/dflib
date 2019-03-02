package com.nhl.dflib.jdbc.connector;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface StatementBinderFactory<T> {

    static JdbcConsumer<Object> objectBinder(StatementPosition p) {
        return p::bind;
    }

    static JdbcConsumer<Object> dateBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> o instanceof LocalDate ? Date.valueOf((LocalDate) o) : o);
        return objectBinder(converted);
    }

    static JdbcConsumer<Object> timestampBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> o instanceof LocalDateTime ? Timestamp.valueOf((LocalDateTime) o) : o);
        return objectBinder(converted);
    }

    static JdbcConsumer<Object> timeBinder(StatementPosition p) {
        StatementPosition converted = p.withConverter(o -> o instanceof LocalTime ? Time.valueOf((LocalTime) o) : o);
        return objectBinder(converted);
    }

    JdbcConsumer<Object> binder(StatementPosition p);
}
