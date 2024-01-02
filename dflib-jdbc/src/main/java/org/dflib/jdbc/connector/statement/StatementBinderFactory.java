package org.dflib.jdbc.connector.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public interface StatementBinderFactory {

    StatementBinder createBinder(PreparedStatement statement) throws SQLException;
}
