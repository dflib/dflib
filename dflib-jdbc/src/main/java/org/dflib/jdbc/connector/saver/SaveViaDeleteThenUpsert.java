package org.dflib.jdbc.connector.saver;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.TableDeleter;
import org.dflib.jdbc.connector.metadata.TableFQName;

/**
 * @since 0.8
 */
public class SaveViaDeleteThenUpsert extends SaveViaUpsert {

    public SaveViaDeleteThenUpsert(JdbcConnector connector, TableFQName tableName, String[] keyColumns) {
        super(connector, tableName, keyColumns);
    }

    @Override
    protected boolean shouldDelete(DataFrame df) {
        return true;
    }

    @Override
    protected int doDelete(JdbcConnector connector, DataFrame df) {
        DataFrame keyDf = keyValues(df);
        return new TableDeleter(connector, tableName).neq(keyDf).delete();
    }
}
