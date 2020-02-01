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
    public int[] update(Connection c) throws SQLException {

        logger.log(sql);

        int[] updateCounts = new int[1];
        try (PreparedStatement st = c.prepareStatement(sql)) {
            updateCounts[0] = st.executeUpdate();
        }

        return updateCounts;
    }
}
