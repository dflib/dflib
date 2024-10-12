package org.dflib.jdbc.connector.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementBinderFactory {

    StatementBinder createBinder(PreparedStatement statement) throws SQLException;
}
