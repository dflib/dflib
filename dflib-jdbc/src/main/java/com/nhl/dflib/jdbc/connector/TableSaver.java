package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class TableSaver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSaver.class);

    protected JdbcConnector connector;
    private String tableName;
    private boolean deleteTableData;
    private String rowNumberColumn;

    // TODO: use cases:
    //  + Append to an existing table
    //  + Override an existing table (delete from / insert)
    //  + Store DataFrame row number in a column (may work as PK generator)
    //  * Create new table (with fixed name | with generated name)

    public TableSaver(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.tableName = tableName;
    }

    public TableSaver deleteTableData() {
        this.deleteTableData = true;
        return this;
    }

    public TableSaver storeRowNumber(String rowNumberColumn) {
        this.rowNumberColumn = rowNumberColumn;
        return this;
    }

    public TableSaver save(DataFrame df) {
        try (Connection c = connector.getConnection()) {

            if (deleteTableData) {
                createDeleteStatement(c).update(c);
            }

            DataFrame toSave = rowNumberColumn != null
                    ? df.addColumn(rowNumberColumn, rowIndexer())
                    : df;

            createInsertStatement(c, toSave).update(c);
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }

        return this;
    }

    protected UpdateStatement createDeleteStatement(Connection c) {
        String sql = "delete from " + connector.quoteIdentifier(c, tableName);
        logSql(sql);
        return new UpdateStatementNoParams(sql);
    }

    protected UpdateStatement createInsertStatement(Connection c, DataFrame df) throws SQLException {

        StringBuilder sql = new StringBuilder("insert into ")
                .append(connector.quoteIdentifier(c, tableName))
                .append(" (");

        // append columns
        IndexPosition[] positions = df.getColumns().getPositions();
        int len = positions.length;

        for (int i = 0; i < positions.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }

            sql.append(connector.quoteIdentifier(c, positions[i].name()));
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

        return c.getMetaData().supportsBatchUpdates()
                ? new UpdateStatementBatch(sqlString, df, connector::createBinder)
                : new UpdateStatementNoBatch(sqlString, df, connector::createBinder);
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