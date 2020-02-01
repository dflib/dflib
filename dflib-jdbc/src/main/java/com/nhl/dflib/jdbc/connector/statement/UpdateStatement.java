package com.nhl.dflib.jdbc.connector.statement;

import java.sql.Connection;
import java.sql.SQLException;

public interface UpdateStatement {

    int[] update(Connection c) throws SQLException;
}
