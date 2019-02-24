package com.nhl.dflib.jdbc.connector;

import java.sql.SQLException;

public interface JdbcOperation<T, R> {

    R exec(T in) throws SQLException;
}
