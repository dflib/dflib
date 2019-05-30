package com.nhl.dflib.jdbc.connector;

import java.sql.SQLException;

/**
 * @since 0.6
 */
@FunctionalInterface
public interface StatementPositionBinder {

    void bind(Object o) throws SQLException;
}
