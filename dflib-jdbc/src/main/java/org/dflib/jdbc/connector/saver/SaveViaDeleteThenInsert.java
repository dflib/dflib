package org.dflib.jdbc.connector.saver;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.TableDeleter;
import org.dflib.jdbc.connector.metadata.TableFQName;

public class SaveViaDeleteThenInsert extends SaveViaInsert {

    public SaveViaDeleteThenInsert(JdbcConnector connector, TableFQName tableName, int batchSize) {
        super(connector, tableName, batchSize);
    }

    @Override
    protected boolean shouldDelete(DataFrame df) {
        return true;
    }

    @Override
    protected int doDelete(JdbcConnector connector, DataFrame df) {
        return new TableDeleter(connector, tableName).delete();
    }
}
