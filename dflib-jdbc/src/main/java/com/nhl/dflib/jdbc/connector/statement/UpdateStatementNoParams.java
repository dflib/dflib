package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.jdbc.connector.SqlLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatementNoParams implements UpdateStatement {

    private String sql;
    private SqlLogger logger;

    public UpdateStatementNoParams(String sql, SqlLogger logger) {
        this.sql = sql;
        this.logger = logger;
    }

    @Override
    public void update(Connection c) throws SQLException {

        logger.log(sql);

        try (PreparedStatement st = c.prepareStatement(sql)) {
            st.executeUpdate();
        }
    }
}
