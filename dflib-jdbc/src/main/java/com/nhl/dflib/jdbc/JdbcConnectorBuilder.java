package com.nhl.dflib.jdbc;

import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.datasource.SimpleDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

public class JdbcConnectorBuilder {

    private String userName;
    private String password;
    private String driver;
    private String url;

    public JdbcConnectorBuilder(String url) {
        this.url = url;
    }

    public JdbcConnectorBuilder userName(String userName) {
        this.userName = userName;
        return this;
    }

    public JdbcConnectorBuilder password(String password) {
        this.password = password;
        return this;
    }

    public JdbcConnectorBuilder driver(String driverClassName) {
        this.driver = driverClassName;
        return this;
    }

    public JdbcConnector build() {
        return new JdbcConnector(buildDataSource());
    }

    private DataSource buildDataSource() {
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
