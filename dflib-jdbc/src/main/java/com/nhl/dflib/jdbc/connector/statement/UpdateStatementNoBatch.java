package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.connector.SqlLogger;
import com.nhl.dflib.row.RowProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatementNoBatch implements UpdateStatement {

    private String sql;
    private DataFrame paramsBatch;
    private StatementBinderFactory binderFactory;
    private SqlLogger logger;

    public UpdateStatementNoBatch(
            String sql,
            DataFrame paramsBatch,
            StatementBinderFactory binderFactory,
            SqlLogger logger) {

        this.sql = sql;
        this.paramsBatch = paramsBatch;
        this.binderFactory = binderFactory;
        this.logger = logger;
    }

    @Override
    public void update(Connection c) throws SQLException {

        logger.log(sql, paramsBatch);

        try (PreparedStatement st = c.prepareStatement(sql)) {

            StatementBinder binder = binderFactory.createBinder(st);

            for (RowProxy row : paramsBatch) {
                binder.bind(row);
                st.executeUpdate();
            }
        }
    }
}
