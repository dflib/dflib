package com.nhl.dflib.jdbc.connector;

import java.sql.SQLException;

@FunctionalInterface
public interface JdbcConsumer<T> {

    void accept(T t) throws SQLException;
}
