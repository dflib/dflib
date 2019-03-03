package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.SQLException;

public interface UpdateStatement {

    void update(Connection c) throws SQLException;
}
