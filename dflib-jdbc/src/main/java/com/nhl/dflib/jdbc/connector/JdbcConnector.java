package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.table.JdbcTableLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * A thin abstraction on top of the JDBC DataSource intended to smoothen DB-specific syntax issues.
 */
public class JdbcConnector {

    private DataSource dataSource;
    private IdentifierQuoter quoter;
    private ValueReaderFactory defaultValueReaderFactory;
    private Map<Integer, ValueReaderFactory> valueReaderFactories;

    public JdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;

        this.defaultValueReaderFactory = ValueReaderFactory::objectReader;

        // default type conversions... custom conversions can be done via the DataFrame
        this.valueReaderFactories = new HashMap<>();
        this.valueReaderFactories.put(Types.DATE, ValueReaderFactory::dateReader);
        this.valueReaderFactories.put(Types.TIME, ValueReaderFactory::timeReader);
        this.valueReaderFactories.put(Types.TIMESTAMP, ValueReaderFactory::timestampReader);
    }

    public JdbcTableLoader tableLoader(String tableName) {
        return new JdbcTableLoader(this, tableName);
    }

    public JdbcOperation<ResultSet, Object> getValueReader(int type, int pos) {
        return valueReaderFactories.getOrDefault(type, defaultValueReaderFactory).reader(pos);
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

    private IdentifierQuoter getOrCreateQuoter(Connection connection) {

        if (quoter == null) {
            quoter = createQuoter(connection);
        }

        return quoter;
    }

    private IdentifierQuoter createQuoter(Connection connection) {
        String identifierQuote;
        try {
            identifierQuote = connection.getMetaData().getIdentifierQuoteString();
        } catch (SQLException e) {
            throw new RuntimeException("Error reading test DB metadata");
        }

        // if no quotations are supported, per JDBC spec the returned value is space

        return " ".equals(identifierQuote)
                ? IdentifierQuoter.noQuote()
                : IdentifierQuoter.forQuoteSymbol(identifierQuote);
    }
}
