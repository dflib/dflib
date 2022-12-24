package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.loader.JdbcColumnBuilderFactory;
import com.nhl.dflib.jdbc.connector.metadata.DbMetadata;
import com.nhl.dflib.jdbc.datasource.SimpleDataSource;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JdbcConnectorBuilder {

    private String userName;
    private String password;
    private String driver;
    private String url;
    private DataSource dataSource;
    private Map<Integer, JdbcColumnBuilderFactory> columnBuilderFactories;

    public JdbcConnectorBuilder url(String url) {
        this.url = url;
        return this;
    }

    public JdbcConnectorBuilder userName(String userName) {
        this.dataSource = null;
        this.userName = userName;
        return this;
    }

    public JdbcConnectorBuilder password(String password) {
        this.dataSource = null;
        this.password = password;
        return this;
    }

    public JdbcConnectorBuilder driver(String driverClassName) {
        this.dataSource = null;
        this.driver = driverClassName;
        return this;
    }

    /**
     * @since 0.8
     */
    public JdbcConnectorBuilder dataSource(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.userName = null;
        this.password = null;
        this.driver = null;
        this.url = null;
        return this;
    }

    /**
     * Adds a custom {@link JdbcColumnBuilderFactory} to handle reading data columns of a given JDBC type.
     *
     * @param columnJdbcType a type of column defined in {@link Types}.
     * @param factory        a factory for column builders that should be associated with the provided type
     * @see JdbcColumnBuilderFactory for a collection of commonly-used factories.
     * @since 0.8
     */
    public <T> JdbcConnectorBuilder addColumnBuilderFactory(int columnJdbcType, JdbcColumnBuilderFactory<T> factory) {
        if (this.columnBuilderFactories == null) {
            this.columnBuilderFactories = new HashMap<>();
        }

        this.columnBuilderFactories.put(columnJdbcType, factory);
        return this;
    }

    public JdbcConnector build() {
        DataSource dataSource = this.dataSource != null ? this.dataSource : createDataSource();
        return new DefaultJdbcConnector(dataSource, DbMetadata.create(dataSource), createColumnBuilderFactories());
    }

    private Map<Integer, JdbcColumnBuilderFactory> createColumnBuilderFactories() {
        // add standard factories unless already defined by the user
        Map<Integer, JdbcColumnBuilderFactory> factories = new HashMap<>();

        factories.put(Types.DATE, JdbcColumnBuilderFactory::dateCol);
        factories.put(Types.TIME, JdbcColumnBuilderFactory::timeCol);
        factories.put(Types.TIMESTAMP, JdbcColumnBuilderFactory::timestampCol);

        if (this.columnBuilderFactories != null) {
            factories.putAll(columnBuilderFactories);
        }

        return factories;
    }

    private DataSource createDataSource() {
        Driver driver = createDriver();
        return new SimpleDataSource(url, userName, password, driver);
    }

    private Driver createDriver() {

        if (driver == null) {
            return null;
        }

        try {
            return (Driver) Class.forName(driver).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error instantiating driver: " + driver, e);
        }
    }
}
