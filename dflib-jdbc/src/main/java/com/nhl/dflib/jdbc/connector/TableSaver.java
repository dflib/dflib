package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;
import com.nhl.dflib.jdbc.connector.metadata.DbTableMetadata;
import com.nhl.dflib.row.RowProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class TableSaver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSaver.class);

    protected JdbcConnector connector;
    private String tableName;
    private String rowNumberColumn;

    // save strategy-defining vars
    private boolean deleteTableData;
    private boolean mergeByPk;
    private String[] mergeByColumns;


    // TODO: use cases:
    //  + Append to an existing table
    //  + Store DataFrame row number in a column (may work as PK generator)
    //  * Create new table (with fixed name | with generated name)

    public TableSaver(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    /**
     * Configures saver to delete all table rows before performing insert operation.
     *
     * @return this saver instance
     */
    public TableSaver deleteTableData() {
        this.deleteTableData = true;
        return this;
    }

    /**
     * Configures saver to perform save as "merge" (aka "upsert") instead of "insert" done by default. TableSaver would
     * identify PK column(s) in the table, and will match them against the DataFrame to be saved. For matching rows an
     * UPDATE will be run, and for all others INSERT will be run. If {@link #deleteTableData} was also specified, this
     * setting has no effect, and a full INSERT is performed.
     *
     * @return this saver instance
     * @since 0.6
     */
    public TableSaver mergeByPk() {
        this.mergeByPk = true;
        this.mergeByColumns = null;
        return this;
    }

    /**
     * Configures saver to perform save as "merge" (aka "upsert") instead of "insert" done by default. TableSaver would
     * use provided column names to match DB values against the DataFrame to be saved. For matching rows an UPDATE will be
     * run, and for all others INSERT will be run. If {@link #deleteTableData} was also specified, this setting has no
     * effect, and a full INSERT is performed.
     *
     * @return this saver instance
     * @since 0.6
     */
    public TableSaver mergeByColumns(String... columns) {
        this.mergeByPk = false;
        this.mergeByColumns = columns;
        return this;
    }

    /**
     * @deprecated since 0.6. THis functionality can be emulated simply by adding a row number column to the saved DataFrame.
     */
    @Deprecated
    public TableSaver storeRowNumber(String rowNumberColumn) {
        this.rowNumberColumn = rowNumberColumn;
        return this;
    }

    public void save(DataFrame df) {
        if (df.height() == 0 && !deleteTableData) {
            LOGGER.info("Empty DataFrame and no delete requested. Save does nothing.");
            return;
        }

        try (Connection c = connector.getConnection()) {

            if (deleteTableData) {
                connector.createStatementBuilder(createDeleteStatement()).update(c);
            }

            if (df.height() > 0) {

                DataFrame toSave = rowNumberColumn != null
                        ? df.addColumn(rowNumberColumn, rowIndexer())
                        : df;

                connector.createStatementBuilder(createInsertStatement(toSave))

                        // use param descriptors from metadata, as (1) we can and (b) some DBs don't support real
                        // metadata in PreparedStatements. See e.g. https://github.com/nhl/dflib/issues/49

                        .paramDescriptors(fixedParams(toSave.getColumnsIndex()))
                        .bindBatch(toSave)
                        .update(c);

            } else {
                LOGGER.info("Empty DataFrame. Skipping insert.");
            }

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error storing data in DB", e);
        }
    }

    protected String createDeleteStatement() {
        String sql = "delete from " + connector.quoteIdentifier(tableName);
        logSql(sql);
        return sql;
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

    protected void logSql(String sql) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Storing DataFrame... {}", sql);
            // do not log SQL parameters (i.e. DataFrame)
        }
    }

    protected RowToValueMapper<Integer> rowIndexer() {
        return new RowToValueMapper<Integer>() {

            int i = 1;

            @Override
            public Integer map(RowProxy row) {
                return i++;
            }
        };
    }
}
