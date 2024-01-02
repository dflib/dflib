package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.metadata.DbColumnMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.saver.FullSync;
import org.dflib.jdbc.connector.saver.SaveViaDeleteThenInsert;
import org.dflib.jdbc.connector.saver.SaveViaInsert;
import org.dflib.jdbc.connector.saver.SaveViaUpsert;
import org.dflib.jdbc.connector.saver.TableSaveStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class TableSaver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSaver.class);

    protected JdbcConnector connector;
    private TableFQName tableName;

    // save strategy-defining vars
    private boolean deleteTableData;
    private boolean deleteUnmatchedRows;
    private boolean mergeByPk;
    private String[] mergeByColumns;

    public TableSaver(JdbcConnector connector, TableFQName tableName) {
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
     * Configures saver to delete table rows that did not match any rows in the saved DataFrame. If
     * {@link #deleteTableData()} was specified, this setting has no effect, as all rows will be deleted prior to
     * saving the data anyways. If no merge strategy (by PK or by columns) was specified, this setting also has no effect.
     *
     * @return this saver instance
     */
    public TableSaver deleteUnmatchedRows() {
        this.deleteUnmatchedRows = true;
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
        this.mergeByColumns = Objects.requireNonNull(columns);

        if (columns.length == 0) {
            throw new IllegalArgumentException("Empty 'mergeBy' columns");
        }

        return this;
    }

    public SaveStats save(DataFrame df) {
        LOGGER.debug("saving DataFrame...");
        return new SaveStats(createSaveStrategy().save(df));
    }

    protected TableSaveStrategy createSaveStrategy() {
        
        // if delete is in effect, we don't need the UPDATE part of "UPSERT"
        if (deleteTableData) {
            return new SaveViaDeleteThenInsert(connector, tableName);
        }

        if (!mergeByPk && mergeByColumns == null) {
            return new SaveViaInsert(connector, tableName);
        }

        String[] keyColumns = mergeByPk ? getPkColumns() : mergeByColumns;
        return deleteUnmatchedRows
                ? new FullSync(connector, tableName, keyColumns)
                : new SaveViaUpsert(connector, tableName, keyColumns);
    }

    protected String[] getPkColumns() {

        DbColumnMetadata[] pk = connector.getMetadata().getTable(tableName).getPkColumns();

        int len = pk.length;
        if (len == 0) {
            throw new IllegalStateException("Table '" + tableName + "' does not define a PK");
        }

        String[] pkNames = new String[len];

        for (int i = 0; i < len; i++) {
            pkNames[i] = pk[i].getName();
        }

        return pkNames;
    }
}
