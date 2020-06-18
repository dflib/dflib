package com.nhl.dflib.jdbc.connector.tx;

import com.nhl.dflib.jdbc.connector.*;
import com.nhl.dflib.jdbc.connector.loader.ColumnBuilder;
import com.nhl.dflib.jdbc.connector.metadata.DbMetadata;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.jdbc.connector.statement.ValueConverterFactory;
import com.nhl.dflib.jdbc.datasource.TxDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @since 0.6
 */
public class TxJdbcConnector implements JdbcConnector {

    private Connection connection;
    private JdbcConnector delegate;

    public TxJdbcConnector(JdbcConnector delegate, Connection connection) {
        this.connection = connection;
        this.delegate = delegate;
    }

    @Override
    public TableSaver tableSaver(String tableName) {
        return tableSaver(getMetadata().parseTableName(tableName));
    }

    /**
     * @since 0.7
     */
    @Override
    public TableSaver tableSaver(TableFQName tableName) {
        return new TableSaver(this, tableName);
    }

    @Override
    public TableLoader tableLoader(String tableName) {
        return tableLoader(getMetadata().parseTableName(tableName));
    }

    /**
     * @since 0.7
     */
    @Override
    public TableLoader tableLoader(TableFQName tableName) {
        return new TableLoader(this, tableName);
    }

    @Override
    public TableDeleter tableDeleter(String tableName) {
        return tableDeleter(getMetadata().parseTableName(tableName));
    }

    @Override
    public TableDeleter tableDeleter(TableFQName tableName) {
        return new TableDeleter(this, tableName);
    }

    @Override
    public SqlLoader sqlLoader(String sql) {
        return new SqlLoader(this, sql);
    }

    @Override
    public SqlSaver sqlSaver(String sql) {
        return new SqlSaver(this, sql);
    }

    @Override
    public StatementBuilder createStatementBuilder(String sql) {
        return new StatementBuilder(this).sql(sql);
    }

    @Override
    public DbMetadata getMetadata() {
        return delegate.getMetadata();
    }

    @Override
    public DataSource getDataSource() {
        return new TxDataSource(connection, delegate.getDataSource());
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String quoteIdentifier(String bareIdentifier) {
        return delegate.quoteIdentifier(bareIdentifier);
    }

    /**
     * @since 0.7
     */
    @Override
    public String quoteTableName(TableFQName tableName) {
        return delegate.quoteTableName(tableName);
    }

    @Override
    public ColumnBuilder<?> createColumnReader(int pos, int type, boolean mandatory) {
        return delegate.createColumnReader(pos, type, mandatory);
    }

    @Override
    public SqlLogger getSqlLogger() {
        return delegate.getSqlLogger();
    }

    @Override
    public ValueConverterFactory getBindConverterFactory() {
        return delegate.getBindConverterFactory();
    }
}
