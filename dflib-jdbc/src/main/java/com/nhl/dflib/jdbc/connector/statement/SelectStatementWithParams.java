package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.connector.JdbcFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectStatementWithParams implements SelectStatement {

    private String sql;
    private Series<?> params;
    private StatementBinderFactory binderFactory;

    public SelectStatementWithParams(
            String sql,
            Series<?> params,
            StatementBinderFactory binderFactory) {

        this.sql = sql;
        this.params = params;
        this.binderFactory = binderFactory;
    }

    @Override
    public <T> T select(Connection connection, JdbcFunction<ResultSet, T> resultReader) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                return resultReader.apply(rs);
            }
        }
    }

    private void bind(PreparedStatement statement) throws SQLException {
        if (params.size() > 0) {
            binderFactory.createBinder(statement).bind(params);
        }
    }
}
