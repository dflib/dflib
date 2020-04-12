package com.nhl.dflib.jdbc.unit;

import io.bootique.jdbc.DataSourceListener;
import io.bootique.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class DbInitializer implements DataSourceListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbInitializer.class);

    private String sqlFile;
    private String delimiter;

    public DbInitializer(String sqlFile, String delimiter) {
        this.sqlFile = sqlFile;
        this.delimiter = delimiter;
    }

    @Override
    public void afterStartup(String name, String jdbcUrl, DataSource dataSource) {

        URL sqlFileUrl = new ResourceFactory(sqlFile).getUrl();
        LOGGER.info("Updating database from {}", sqlFile);

        executeSQL(dataSource, loadSqlStatements(sqlFileUrl));
    }

    private Collection<String> loadSqlStatements(URL sqlFileUrl) {
        try (Scanner scanner = new Scanner(sqlFileUrl.openStream()).useDelimiter(delimiter)) {
            return loadSqlStatements(scanner);
        } catch (IOException e) {
            throw new RuntimeException("Error reading SQL file", e);
        }
    }

    private Collection<String> loadSqlStatements(Scanner scanner) {

        List<String> statements = new ArrayList<>();

        while (scanner.hasNext()) {
            statements.add(scanner.next());
        }

        return statements;
    }

    private void executeSQL(DataSource dataSource, Collection<String> statements) {

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            for (String statement : statements) {
                executeSQLWithExceptions(connection, statement);
            }

        } catch (SQLException e) {
            LOGGER.error("Error executing SQL statements", e);
            throw new RuntimeException(e);
        }
    }

    private void executeSQLWithExceptions(Connection connection, String sql) throws SQLException {

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
