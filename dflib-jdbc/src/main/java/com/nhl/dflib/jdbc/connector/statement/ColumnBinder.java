package com.nhl.dflib.jdbc.connector.statement;

import java.sql.SQLException;

/**
 * @since 0.9
 */
public interface ColumnBinder {

    void bind(Object o) throws SQLException;
}
