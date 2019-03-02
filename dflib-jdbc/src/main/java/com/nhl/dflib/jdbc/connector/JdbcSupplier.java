package com.nhl.dflib.jdbc.connector;

import java.sql.SQLException;

@FunctionalInterface
public interface JdbcSupplier<T> {

    T get() throws SQLException;
}
