package org.dflib.jdbc.connector.statement;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.SqlLogger;
import org.dflib.row.RowProxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatementNoBatch implements UpdateStatement {

    private final String sql;
    private final DataFrame paramsBatch;
    private final StatementBinderFactory binderFactory;
    private final SqlLogger logger;

    public UpdateStatementNoBatch(
            String sql,
            DataFrame paramsBatch,
            StatementBinderFactory binderFactory,
            SqlLogger logger) {

        this.sql = sql;
        this.paramsBatch = paramsBatch;
        this.binderFactory = binderFactory;
        this.logger = logger;
    }

    @Override
    public int[] update(Connection c) throws SQLException {

        logger.log(sql, paramsBatch);

        int len = paramsBatch.height();
        int[] updateCounts = new int[len];

        try (PreparedStatement st = c.prepareStatement(sql)) {

            StatementBinder binder = binderFactory.createBinder(st);
            int i = 0;

            for (RowProxy row : paramsBatch) {
                binder.bind(row);
                updateCounts[i++] = st.executeUpdate();
            }
        }

        return updateCounts;
    }
}
