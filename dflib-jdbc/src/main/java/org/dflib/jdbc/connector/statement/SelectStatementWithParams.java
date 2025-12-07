package org.dflib.jdbc.connector.statement;

import org.dflib.Series;
import org.dflib.jdbc.connector.JdbcFunction;
import org.dflib.jdbc.connector.SqlLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectStatementWithParams implements SelectStatement {

    private final String sql;
    private final Series<?> params;
    private final StatementBinderFactory binderFactory;
    private final SqlLogger logger;

    public SelectStatementWithParams(
            String sql,
            Series<?> params,
            StatementBinderFactory binderFactory,
            SqlLogger logger) {

        this.sql = sql;
        this.params = params;
        this.binderFactory = binderFactory;
        this.logger = logger;
    }

    @Override
    public <T> T select(Connection connection, JdbcFunction<ResultSet, T> resultReader) throws SQLException {

        logger.log(sql, params);

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
