package org.dflib.jdbc.connector.tx;

import org.dflib.Extractor;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.jdbc.connector.SqlLoader;
import org.dflib.jdbc.connector.SqlLogger;
import org.dflib.jdbc.connector.SqlSaver;
import org.dflib.jdbc.connector.StatementBuilder;
import org.dflib.jdbc.connector.TableDeleter;
import org.dflib.jdbc.connector.TableLoader;
import org.dflib.jdbc.connector.TableSaver;
import org.dflib.jdbc.connector.metadata.DbMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.statement.ValueConverterFactory;
import org.dflib.jdbc.datasource.TxDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A connector that provides the same connection to all consumers, opening it lazily when first requested.
 *
 * @since 0.6
 */
public class TxJdbcConnector implements JdbcConnector, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxJdbcConnector.class);

    private final JdbcConnector delegate;
    private final boolean nestedTx;

    private TxIsolation isolation;
    private volatile TxConnectionWrapper connection;

    public TxJdbcConnector(JdbcConnector delegate, TxIsolation isolation) {
        this.isolation = isolation;
        this.delegate = delegate;
        this.nestedTx = delegate instanceof TxJdbcConnector;
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
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = createAndInitConnection();
                }
            }
        }

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

    /**
     * @since 0.16
     */
    @Override
    public Extractor<ResultSet, ?> createExtractor(int resultSetPosition, int type, boolean mandatory) {
        return delegate.createExtractor(resultSetPosition, type, mandatory);
    }

    @Override
    public SqlLogger getSqlLogger() {
        return delegate.getSqlLogger();
    }

    @Override
    public ValueConverterFactory getBindConverterFactory() {
        return delegate.getBindConverterFactory();
    }

    protected TxConnectionWrapper createAndInitConnection() {

        Connection connection = delegate.getConnection();

        if (isolation != null) {
            try {
                connection.setTransactionIsolation(isolation.value);
            } catch (SQLException e) {
                throw new RuntimeException("Error setting isolation level", e);
            }
        }

        return new TxConnectionWrapper(connection);
    }

    protected void commit() {
        connectionOp(Connection::commit);
    }

    protected void rollback() {
        connectionOp(Connection::rollback);
    }

    @Override
    public void close() {
        connectionOp(Connection::close);
        this.connection = null;
    }

    protected void connectionOp(JdbcConsumer op) {

        // TODO: both checking for nested connection and unwrapping it is a hack .. perhaps we can express the
        //   desired behavior by properly implementing the connection wrapper?

        if (nestedTx) {
            LOGGER.debug("Nested transaction... Ignoring a request to modify the connection");
            return;
        }

        Connection connection = this.connection;
        if (connection != null) {
            try {
                // connector-level commit or rollback must be performed on the underlying real connection, not the Tx wrapper
                Connection realConnection = connection.unwrap(Connection.class);
                op.consume(realConnection);
            } catch (SQLException e) {
                throw new RuntimeException("Error processing connection", e);
            }
        }
    }

    interface JdbcConsumer {
        void consume(Connection c) throws SQLException;
    }
}
