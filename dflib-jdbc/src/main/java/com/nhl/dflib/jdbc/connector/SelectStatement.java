package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SelectStatement {

    <T> T select(Connection connection, JdbcFunction<ResultSet, T> resultReader) throws SQLException;

}
