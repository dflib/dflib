package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;
import com.nhl.dflib.jdbc.connector.metadata.DbTableMetadata;

import java.sql.Connection;

/**
 * @since 0.6
 */
public class SaveViaInsert extends TableSaveStrategy {

    public SaveViaInsert(JdbcConnector connector, String tableName) {
        super(connector, tableName);
    }

    @Override
    protected void doSave(Connection connection, DataFrame df) {
        connector.createStatementBuilder(createInsertStatement(df))

                // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                // metadata in PreparedStatements. See e.g. https://github.com/nhl/dflib/issues/49

                .paramDescriptors(fixedParams(df.getColumnsIndex()))
                .bindBatch(df)
                .update(connection);
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
                .append(connector.quoteIdentifier(tableName))
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

        String sqlString = sql.toString();
        logSql(sqlString);
        return sqlString;
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
