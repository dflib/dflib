package com.nhl.dflib.jdbc.connector.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatementNoParams implements UpdateStatement {

    private String sql;

    public UpdateStatementNoParams(String sql) {
        this.sql = sql;
    }

    @Override
    public void update(Connection c) throws SQLException {
        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.executeUpdate();
        }
    }
}
