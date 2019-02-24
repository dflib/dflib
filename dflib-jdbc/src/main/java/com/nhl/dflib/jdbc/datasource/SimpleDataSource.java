package com.nhl.dflib.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;

// based on Cayenne DriverDataSource
public class SimpleDataSource implements DataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataSource.class);

    protected Driver driver;
    protected String url;
    protected String userName;
    protected String password;

    public SimpleDataSource(String url, String userName, String password, Driver driver) {
        this.url = Objects.requireNonNull(url);
        this.driver = driver;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(userName, password);
    }

    @Override
    public Connection getConnection(String userName, String password) throws SQLException {
        try {

            LOGGER.debug("Connecting to '{}' as '{}'", url, userName);
            Connection c;

            if (driver == null) {
                c = DriverManager.getConnection(url, userName, password);
            } else {
                Properties connectProperties = new Properties();

                if (userName != null) {
                    connectProperties.put("user", userName);
                }

                if (password != null) {
                    connectProperties.put("password", password);
                }
                c = driver.connect(url, connectProperties);
            }

            // some drivers (Oracle) return null connections instead of throwing an exception...

            if (c == null) {
                throw new SQLException("Can't establish connection: " + url);
            }

            LOGGER.debug("Connected");

            return c;
        } catch (SQLException ex) {
            LOGGER.warn("Connection failure", ex);
            throw ex;
        }
    }

    @Override
    public int getLoginTimeout() {
        return -1;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        // noop
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported");
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
