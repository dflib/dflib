package org.dflib.jdbc.connector.statement;

import org.dflib.jdbc.connector.JdbcFunction;
import org.dflib.jdbc.connector.SqlLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectStatementNoParams implements SelectStatement {

    private final String sql;
    private final SqlLogger logger;

    public SelectStatementNoParams(String sql, SqlLogger logger) {
        this.logger = logger;
        this.sql = sql;
    }

    @Override
    public <T> T select(Connection connection, JdbcFunction<ResultSet, T> resultReader) throws SQLException {

        logger.log(sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {

                return resultReader.apply(rs);
            }
        }
    }
}
