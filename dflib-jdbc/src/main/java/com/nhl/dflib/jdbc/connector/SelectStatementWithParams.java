package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SelectStatementWithParams implements SelectStatement {

    private String sql;
    private Object[] params;
    private JdbcFunction<PreparedStatement, StatementBinder> binderFactory;

    public SelectStatementWithParams(
            String sql,
            Object[] params,
            JdbcFunction<PreparedStatement, StatementBinder> binderFactory) {

        this.sql = sql;
        this.params = params;
        this.binderFactory = binderFactory;
    }

    @Override
    public PreparedStatement toJdbcStatement(Connection connection) throws SQLException {

        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            bind(statement);
            return statement;
        } catch (SQLException e) {

            try {
                statement.close();
            } catch (SQLException ec) {
                // ignore
            }

            throw e;
        }
    }

    private void bind(PreparedStatement statement) throws SQLException {
        if (params.length > 0) {
            binderFactory.apply(statement).bind(params);
        }
    }
}
