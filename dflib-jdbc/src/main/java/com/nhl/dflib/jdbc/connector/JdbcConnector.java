package com.nhl.dflib.jdbc.connector;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private StatementBinderFactory defaultStatementBinderFactory;
    private Map<Integer, StatementBinderFactory> statementBinderFactories;

    public JdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;

        this.defaultValueReaderFactory = ValueReaderFactory::objectReader;
        this.valueReaderFactories = new HashMap<>();
        this.valueReaderFactories.put(Types.DATE, ValueReaderFactory::dateReader);
        this.valueReaderFactories.put(Types.TIME, ValueReaderFactory::timeReader);
        this.valueReaderFactories.put(Types.TIMESTAMP, ValueReaderFactory::timestampReader);

        this.defaultStatementBinderFactory = StatementBinderFactory::objectBinder;
        this.statementBinderFactories = new HashMap<>();
        this.statementBinderFactories.put(Types.DATE, StatementBinderFactory::dateBinder);
        this.statementBinderFactories.put(Types.TIME, StatementBinderFactory::timeBinder);
        this.statementBinderFactories.put(Types.TIMESTAMP, StatementBinderFactory::timestampBinder);
    }

    public TableLoader fromTable(String tableName) {
        return new TableLoader(this, tableName);
    }

    /**
     * Creates a new {@link SqlLoader} to load DataFrame from a custom SQL query.
     *
     * @param sql a parameterized SQL statement that should be run to get the DataFrame data. Format of the SQL String
     *            corresponds to the JDBC {@link java.sql.PreparedStatement}. So e.g. it may contain "?" placeholders
     *            for bound parameters. Bound parameters are then passed via {@link SqlLoader#params(Object...)}.
     * @return a new SqlLoader
     */
    public SqlLoader withSql(String sql) {
        return new SqlLoader(this, sql);
    }

    protected JdbcFunction<ResultSet, Object> getValueReader(int type, int pos) {
        return valueReaderFactories.getOrDefault(type, defaultValueReaderFactory).reader(pos);
    }

    protected JdbcConsumer<PreparedStatement> getStatementBinder(int type, int pos, Object value) {
        return statementBinderFactories
                .getOrDefault(type, defaultStatementBinderFactory)
                .binder(new Binding(type, pos, value));
    }

    protected Connection getConnection() throws SQLException {

        Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {

            try {
                connection.close();
            } catch (SQLException ignored) {
            }

            throw e;
        }

        return connection;
    }

    protected String quoteIdentifier(Connection connection, String bareIdentifier) {
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
