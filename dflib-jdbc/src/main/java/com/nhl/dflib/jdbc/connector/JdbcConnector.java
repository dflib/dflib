package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.JdbcTableLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A thin abstraction on top of the JDBC DataSource intended to smoothen DB-specific syntax issues.
 */
public class JdbcConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConnector.class);

    protected DataSource dataSource;

    protected IdentifierQuotationStrategy quoter;
    protected BindingValueToStringConverter valueToStringConverter;
    protected ObjectValueConverter objectValueConverter;

    public JdbcConnector(DataSource dataSource) {

        LOGGER.debug("JdbcConnector opened...");

        this.dataSource = dataSource;
        this.valueToStringConverter = new BindingValueToStringConverter();
        this.objectValueConverter = new ObjectValueConverter();
    }

    public JdbcTableLoader table(String tableName) {
        return new JdbcTableLoader(this, tableName);
    }

    public Connection getConnection() {

        Connection connection;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error opening connection", e);
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {

            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
        return connection;
    }

    public String quoteIdentifier(Connection connection, String bareIdentifier) {
        return getOrCreateQuoter(connection).quoted(bareIdentifier);
    }

    private IdentifierQuotationStrategy getOrCreateQuoter(Connection connection) {

        if (quoter == null) {
            quoter = createQuoter(connection);
        }

        return quoter;
    }

    private IdentifierQuotationStrategy createQuoter(Connection connection) {
        String identifierQuote;
        try {
            identifierQuote = connection.getMetaData().getIdentifierQuoteString();
        } catch (SQLException e) {
            throw new RuntimeException("Error reading test DB metadata");
        }

        // if no quotations are supported, per JDBC spec the returned value is space

        return " ".equals(identifierQuote)
                ? IdentifierQuotationStrategy.noQuote()
                : IdentifierQuotationStrategy.forQuoteSymbol(identifierQuote);
    }
}
