package org.dflib.jdbc.connector.statement;

import java.sql.SQLException;

public interface ColumnBinder {

    void bind(Object o) throws SQLException;
}
