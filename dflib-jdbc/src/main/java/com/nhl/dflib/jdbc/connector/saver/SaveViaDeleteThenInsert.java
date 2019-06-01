package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.connector.JdbcConnector;

import java.sql.Connection;

/**
 * @since 0.6
 */
public class SaveViaDeleteThenInsert extends SaveViaInsert {

    public SaveViaDeleteThenInsert(JdbcConnector connector, String tableName) {
        super(connector, tableName);
    }

    @Override
    protected boolean shouldSave(DataFrame df) {
        return true;
    }

    @Override
    protected void doSave(Connection connection, DataFrame df) {
        connector.createStatementBuilder(createDeleteStatement()).update(connection);

        if (df.height() > 0) {
            super.doSave(connection, df);
        } else {
            log("Empty DataFrame. Skipping insert.");
        }
    }

    protected String createDeleteStatement() {
        return  "delete from " + connector.quoteIdentifier(tableName);
    }
}
