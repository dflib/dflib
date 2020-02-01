package com.nhl.dflib.jdbc.connector.tx;

import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.SqlLoader;
import com.nhl.dflib.jdbc.connector.SqlLogger;
import com.nhl.dflib.jdbc.connector.SqlUpdater;
import com.nhl.dflib.jdbc.connector.StatementBuilder;
import com.nhl.dflib.jdbc.connector.TableLoader;
import com.nhl.dflib.jdbc.connector.TableSaver;
import com.nhl.dflib.jdbc.connector.metadata.DbMetadata;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.jdbc.connector.statement.ValueConverterFactory;
import com.nhl.dflib.jdbc.datasource.TxDataSource;
import com.nhl.dflib.series.builder.SeriesBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

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
        return new TableLoader(this, getMetadata().parseTableName(tableName));
    }

    /**
     * @since 0.7
     */
    @Override
    public TableLoader tableLoader(TableFQName tableName) {
        return null;
    }

    @Override
    public SqlLoader sqlLoader(String sql) {
        return new SqlLoader(this, sql);
    }

    @Override
    public SqlUpdater sqlUpdater(String sql) {
        return new SqlUpdater(this, sql);
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
    public SeriesBuilder<ResultSet, ?> createColumnReader(int pos, int type, boolean mandatory) {
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
