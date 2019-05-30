package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.row.RowProxy;

import java.sql.SQLException;

public class StatementBinder {

    private StatementPositionBinder[] binders;

    public StatementBinder(StatementPositionBinder[] binders) {
        this.binders = binders;
    }

    public void bind(Object[] values) throws SQLException {

        int len1 = this.binders != null ? this.binders.length : 0;
        int len2 = values != null ? values.length : 0;

        if (len1 != len2) {
            throw new SQLException("Expected " + len1 + " bindings, got " + len2);
        }

        if (len1 > 0) {

            for (int i = 0; i < len1; i++) {
                binders[i].bind(values[i]);
            }
        }
    }

    public void bind(RowProxy values) throws SQLException {

        // unlike "bind(Object[]), will skip bounds checking on RowProxy to avoid extra work when iterating over a DataFrame
        int len = this.binders != null ? this.binders.length : 0;
        for (int i = 0; i < len; i++) {
            binders[i].bind(values.get(i));
        }
    }
}
