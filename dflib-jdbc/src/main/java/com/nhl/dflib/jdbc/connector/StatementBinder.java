package com.nhl.dflib.jdbc.connector;

import java.sql.SQLException;

public class StatementBinder {

    private JdbcConsumer<Object>[] binders;

    public StatementBinder() {
    }

    public StatementBinder(JdbcConsumer<Object>[] binders) {
        this.binders = binders;
    }

    public void bind(Object[] bindings) throws SQLException {

        int len1 = this.binders != null ? this.binders.length : 0;
        int len2 = bindings != null ? bindings.length : 0;

        if (len1 != len2) {
            throw new SQLException("Expected " + len1 + " bindings, got " + len2);
        }

        if (len1 > 0) {

            for (int i = 0; i < len1; i++) {
                binders[i].accept(bindings[i]);
            }
        }
    }
}
