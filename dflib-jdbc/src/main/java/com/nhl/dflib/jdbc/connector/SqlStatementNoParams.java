package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlStatementNoParams implements SqlStatement {

    private String sql;

    public SqlStatementNoParams(String sql) {
        this.sql = sql;
    }

    @Override
    public PreparedStatement toJdbcStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
