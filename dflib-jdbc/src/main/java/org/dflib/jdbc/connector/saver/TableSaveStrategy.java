package org.dflib.jdbc.connector.saver;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.jdbc.SaveOp;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.StatementBuilder;
import org.dflib.jdbc.connector.metadata.DbColumnMetadata;
import org.dflib.jdbc.connector.metadata.DbTableMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.tx.Tx;
import org.dflib.series.SingleValueSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @since 0.6
 */
public abstract class TableSaveStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSaveStrategy.class);

    protected final JdbcConnector connector;
    protected final TableFQName tableName;

    public TableSaveStrategy(JdbcConnector connector, TableFQName tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public Supplier<Series<SaveOp>> save(DataFrame df) {

        boolean shouldDelete = shouldDelete(df);
        boolean shouldInsertOrUpdate = shouldInsertOrUpdate(df);

        if (!shouldDelete && !shouldInsertOrUpdate) {
            log("Nothing to save");
            return () -> new SingleValueSeries<>(SaveOp.skip, df.height());
        }

        return Tx.newTransaction(connector).call(c -> {
            if (shouldDelete) {
                doDelete(c, df);
            }

            return shouldInsertOrUpdate ? doInsertOrUpdate(c, df) : () -> new SingleValueSeries<>(SaveOp.skip, df.height());
        });
    }

    protected boolean shouldDelete(DataFrame df) {
        return false;
    }

    protected boolean shouldInsertOrUpdate(DataFrame df) {
        return df.height() > 0;
    }

    protected abstract Supplier<Series<SaveOp>> doInsertOrUpdate(JdbcConnector connector, DataFrame df);

    protected int doDelete(JdbcConnector connector, DataFrame df) {
        return -1;
    }

    protected int doInsert(JdbcConnector connector, DataFrame df) {

        StatementBuilder builder = connector.createStatementBuilder(createInsertStatement(df))

                // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                // metadata in PreparedStatements. See e.g. https://github.com/dflib/dflib/issues/49

                .paramDescriptors(fixedParams(df.getColumnsIndex()))
                .bindBatch(df);

        try (Connection c = connector.getConnection()) {
            builder.update(c);
        } catch (SQLException e) {
            throw new RuntimeException("Error closing DB connection", e);
        }

        return df.height();
    }

    protected DbColumnMetadata[] fixedParams(Index index) {
        DbTableMetadata tableMetadata = connector.getMetadata().getTable(tableName);

        DbColumnMetadata[] params = new DbColumnMetadata[index.size()];
        for (int i = 0; i < index.size(); i++) {
            params[i] = tableMetadata.getColumn(index.getLabel(i));
        }

        return params;
    }

    protected void log(String line, Object... messageParams) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(line, messageParams);
        }
    }

    private String createInsertStatement(DataFrame df) {

        StringBuilder sql = new StringBuilder("insert into ")
                .append(connector.quoteTableName(tableName))
                .append(" (");

        // append columns
        String[] labels = df.getColumnsIndex().getLabels();
        int len = labels.length;

        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }

            sql.append(connector.quoteIdentifier(labels[i]));
        }

        // append value placeholders
        sql.append(") values (");

        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sql.append(", ");
            }

            sql.append("?");
        }

        sql.append(")");

        return sql.toString();
    }
}
