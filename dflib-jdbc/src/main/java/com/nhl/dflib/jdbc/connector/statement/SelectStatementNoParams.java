package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.jdbc.connector.JdbcFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectStatementNoParams implements SelectStatement {

    private String sql;

    public SelectStatementNoParams(String sql) {
        this.sql = sql;
    }

    @Override
    public <T> T select(Connection connection, JdbcFunction<ResultSet, T> resultReader) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {

                return resultReader.apply(rs);
            }
        }
    }
}
