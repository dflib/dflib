package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.JdbcConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementBinder {

    private JdbcConsumer<PreparedStatement>[] binders;

    public StatementBinder() {
    }

    public StatementBinder(JdbcConsumer<PreparedStatement>[] binders) {
        this.binders = binders;
    }

    public void bind(PreparedStatement statement) throws SQLException {
        if (binders != null) {
            for (JdbcConsumer<PreparedStatement> b : binders) {
                b.accept(statement);
            }
        }
    }
}
