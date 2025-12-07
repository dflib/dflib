package org.dflib.jdbc.connector.statement;

import org.dflib.Series;
import org.dflib.jdbc.connector.SqlLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatementParamsRow implements UpdateStatement {

    private final String sql;
    private final Series<?> params;
    private final StatementBinderFactory binderFactory;
    private final SqlLogger logger;

    public UpdateStatementParamsRow(
            String sql,
            Series<?> params,
            StatementBinderFactory binderFactory,
            SqlLogger logger) {

        this.sql = sql;
        this.params = params;
        this.binderFactory = binderFactory;
        this.logger = logger;
    }

    @Override
    public int[] update(Connection c) throws SQLException {

        logger.log(sql, params);
        int[] updateCounts = new int[1];

        try (PreparedStatement st = c.prepareStatement(sql)) {
            binderFactory.createBinder(st).bind(params);
            updateCounts[0] = st.executeUpdate();
        }

        return updateCounts;
    }
}
