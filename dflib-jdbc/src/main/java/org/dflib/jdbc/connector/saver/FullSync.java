package org.dflib.jdbc.connector.saver;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.jdbc.SaveOp;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.TableDeleter;
import org.dflib.jdbc.connector.metadata.TableFQName;

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
