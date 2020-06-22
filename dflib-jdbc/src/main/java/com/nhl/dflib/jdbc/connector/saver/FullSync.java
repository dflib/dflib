package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.TableDeleter;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;

import java.util.function.Supplier;

/**
 * @since 0.8
 */
public class FullSync extends SaveViaUpsert {

    public FullSync(JdbcConnector connector, TableFQName tableName, String[] keyColumns) {
        super(connector, tableName, keyColumns);
    }

    @Override
    protected boolean shouldSave(DataFrame df) {
        // even an empty DataFrame causes a save (which will be a pure delete)
        return true;
    }

    @Override
    protected Supplier<Series<SaveOp>> doSave(JdbcConnector connector, DataFrame df) {

        DataFrame keyDf = keyValues(df);
        new TableDeleter(connector, tableName).neq(keyDf).delete();

        // TODO: how do we report "delete" statuses?
        return super.doSave(connector, df, keyDf);
    }
}
