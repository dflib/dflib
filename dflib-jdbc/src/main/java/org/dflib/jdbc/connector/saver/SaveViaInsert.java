package org.dflib.jdbc.connector.saver;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.jdbc.SaveOp;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.series.SingleValueSeries;

import java.util.function.Supplier;

public class SaveViaInsert extends TableSaveStrategy {

    public SaveViaInsert(JdbcConnector connector, TableFQName tableName, int batchSize) {
        super(connector, tableName, batchSize);
    }

    @Override
    protected Supplier<Series<SaveOp>> doInsertOrUpdate(JdbcConnector connector, DataFrame df) {
        doInsert(connector, df);
        return () -> new SingleValueSeries<>(SaveOp.insert, df.height());
    }
}
