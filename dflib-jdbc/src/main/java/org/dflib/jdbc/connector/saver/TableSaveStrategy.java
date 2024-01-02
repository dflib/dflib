package org.dflib.jdbc.connector.saver;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.jdbc.SaveOp;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.tx.Tx;
import org.dflib.series.SingleValueSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @since 0.6
 */
public abstract class TableSaveStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSaveStrategy.class);

    protected JdbcConnector connector;
    protected TableFQName tableName;

    public TableSaveStrategy(JdbcConnector connector, TableFQName tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public Supplier<Series<SaveOp>> save(DataFrame df) {

        if (!shouldSave(df)) {
            return () -> new SingleValueSeries<>(SaveOp.skip, df.height());
        }

        return Tx.newTransaction(connector).call(c -> doSave(c, df));
    }

    protected abstract boolean shouldSave(DataFrame df);

    protected abstract Supplier<Series<SaveOp>> doSave(JdbcConnector connector, DataFrame df);

    protected void log(String line, Object... messageParams) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(line, messageParams);
        }
    }
}
