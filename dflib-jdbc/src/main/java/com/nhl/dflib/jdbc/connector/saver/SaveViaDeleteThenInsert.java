package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.series.EmptySeries;

import java.sql.Connection;
import java.util.function.Supplier;

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
    protected Supplier<Series<SaveOp>> doSave(Connection connection, DataFrame df) {
        connector.createStatementBuilder(createDeleteStatement()).update(connection);

        if (df.height() > 0) {
            return super.doSave(connection, df);
        } else {
            log("Empty DataFrame. Skipping insert.");
            return () -> new EmptySeries<>();
        }
    }

    protected String createDeleteStatement() {
        return "delete from " + connector.quoteIdentifier(tableName);
    }
}
