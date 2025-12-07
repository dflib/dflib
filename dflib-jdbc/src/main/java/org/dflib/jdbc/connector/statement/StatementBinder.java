package org.dflib.jdbc.connector.statement;

import org.dflib.Series;
import org.dflib.row.RowProxy;

import java.sql.SQLException;

public class StatementBinder {

    private final ColumnBinder[] positions;

    public StatementBinder(ColumnBinder[] positions) {
        this.positions = positions;
    }

    public void bind(Series<?> values) throws SQLException {

        int len1 = this.positions != null ? this.positions.length : 0;
        int len2 = values != null ? values.size() : 0;

        if (len1 != len2) {
            throw new SQLException("Expected " + len1 + " bindings, got " + len2);
        }

        if (len1 > 0) {

            for (int i = 0; i < len1; i++) {
                positions[i].bind(values.get(i));
            }
        }
    }

    public void bind(RowProxy values) throws SQLException {

        // unlike "bind(Object[]), will skip bounds checking on RowProxy to avoid extra work when iterating over a DataFrame
        int len = this.positions != null ? this.positions.length : 0;
        for (int i = 0; i < len; i++) {
            positions[i].bind(values.get(i));
        }
    }
}
