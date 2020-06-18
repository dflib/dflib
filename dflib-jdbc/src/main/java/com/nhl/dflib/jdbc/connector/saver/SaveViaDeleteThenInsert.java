package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.TableDeleter;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.jdbc.connector.tx.TxJdbcConnector;
import com.nhl.dflib.series.EmptySeries;

import java.sql.Connection;
import java.util.function.Supplier;

/**
 * @since 0.6
 */
public class SaveViaDeleteThenInsert extends SaveViaInsert {

    public SaveViaDeleteThenInsert(JdbcConnector connector, TableFQName tableName) {
        super(connector, tableName);
    }

    @Override
    protected boolean shouldSave(DataFrame df) {
        return true;
    }

    @Override
    protected Supplier<Series<SaveOp>> doSave(Connection connection, DataFrame df) {

        new TableDeleter(new TxJdbcConnector(connector, connection), tableName).delete();

        if (df.height() > 0) {
            return super.doSave(connection, df);
        } else {
            log("Empty DataFrame. Skipping insert.");
            return () -> new EmptySeries<>();
        }
    }


}
