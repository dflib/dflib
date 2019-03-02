package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlStatement {

    PreparedStatement toJdbcStatement(Connection connection) throws SQLException;

}
