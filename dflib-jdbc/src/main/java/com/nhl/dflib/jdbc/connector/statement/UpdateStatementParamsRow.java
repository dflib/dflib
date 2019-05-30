package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.Series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public class UpdateStatementParamsRow implements UpdateStatement {

    private String sql;
    private Series<?> params;
    private StatementBinderFactory binderFactory;

    public UpdateStatementParamsRow(
            String sql,
            Series<?> params,
            StatementBinderFactory binderFactory) {

        this.sql = sql;
        this.params = params;
        this.binderFactory = binderFactory;
    }

    @Override
    public void update(Connection c) throws SQLException {

        try (PreparedStatement st = c.prepareStatement(sql)) {
            binderFactory.createBinder(st).bind(params);
            st.executeUpdate();
        }
    }
}
