package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatementNoBatch implements UpdateStatement {

    private String sql;
    private DataFrame df;
    private StatementBinderFactory binderFactory;

    public UpdateStatementNoBatch(
            String sql,
            DataFrame df,
            StatementBinderFactory binderFactory) {

        this.sql = sql;
        this.df = df;
        this.binderFactory = binderFactory;
    }

    @Override
    public void update(Connection c) throws SQLException {

        try (PreparedStatement st = c.prepareStatement(sql)) {

            StatementBinder binder = binderFactory.createBinder(st);

            for (RowProxy row : df) {
                binder.bind(row);
                st.executeUpdate();
            }
        }
    }
}
