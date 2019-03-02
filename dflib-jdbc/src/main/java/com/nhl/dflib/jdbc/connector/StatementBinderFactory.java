package com.nhl.dflib.jdbc.connector;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface StatementBinderFactory<T> {

    static JdbcConsumer<PreparedStatement> objectBinder(Binding binding) {
        return st -> binding.bind(st);
    }

    static JdbcConsumer<PreparedStatement> dateBinder(Binding binding) {
        Binding converted = binding.mapValue(o -> o instanceof LocalDate ? Date.valueOf((LocalDate) o) : o);
        return objectBinder(converted);
    }

    static JdbcConsumer<PreparedStatement> timestampBinder(Binding binding) {
        Binding converted = binding.mapValue(o -> o instanceof LocalDateTime ? Timestamp.valueOf((LocalDateTime) o) : o);
        return objectBinder(converted);
    }

    static JdbcConsumer<PreparedStatement> timeBinder(Binding binding) {
        Binding converted = binding.mapValue(o -> o instanceof LocalTime ? Time.valueOf((LocalTime) o) : o);
        return objectBinder(converted);
    }

    JdbcConsumer<PreparedStatement> binder(Binding binding);
}
