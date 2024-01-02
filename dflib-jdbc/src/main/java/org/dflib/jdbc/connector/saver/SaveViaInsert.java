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
import org.dflib.series.SingleValueSeries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @since 0.6
 */
public class SaveViaInsert extends TableSaveStrategy {

    public SaveViaInsert(JdbcConnector connector, TableFQName tableName) {
        super(connector, tableName);
    }

    @Override
    protected Supplier<Series<SaveOp>> doSave(JdbcConnector connector, DataFrame df) {

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

        return () -> new SingleValueSeries<>(SaveOp.insert, df.height());
    }

    @Override
    protected boolean shouldSave(DataFrame df) {
        if (df.height() == 0) {
            log("Empty DataFrame. Save does nothing.");
            return false;
        }

        return true;
    }

    protected String createInsertStatement(DataFrame df) {

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

    protected DbColumnMetadata[] fixedParams(Index index) {
        DbTableMetadata tableMetadata = connector.getMetadata().getTable(tableName);

        DbColumnMetadata[] params = new DbColumnMetadata[index.size()];
        for (int i = 0; i < index.size(); i++) {
            params[i] = tableMetadata.getColumn(index.getLabel(i));
        }

        return params;
    }
}
