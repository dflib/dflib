package com.nhl.dflib.jdbc.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * A DataSource with transaction scope that returns active transaction connection.
 *
 * @since 0.7
 */
public class TxDataSource implements DataSource {

    private Connection txConnection;
    private DataSource delegate;

    public TxDataSource(Connection txConnection, DataSource delegate) {
        this.txConnection = txConnection;
        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() {
        return new TxDataSourceConnection(txConnection);
    }

    @Override
    public Connection getConnection(String username, String password) {
        return new TxDataSourceConnection(txConnection);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
