package com.nhl.dflib.jdbc.connector;

import java.sql.SQLException;

@FunctionalInterface
public interface JdbcFunction<T, R> {

    R apply(T in) throws SQLException;
}
