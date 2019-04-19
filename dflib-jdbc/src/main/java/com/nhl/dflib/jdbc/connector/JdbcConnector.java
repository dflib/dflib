package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.builder.SeriesBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * A thin abstraction on top of the JDBC DataSource intended to smoothen DB-specific syntax issues, value types to JDBC
 * types conversion, etc.
 */
public class JdbcConnector {

    private DataSource dataSource;
    private IdentifierQuoter quoter;

    private SeriesBuilderFactory defaultSeriesBuilderFactory;
    private Map<Integer, SeriesBuilderFactory> mandatorySeriesBuilderFactories;
    private Map<Integer, SeriesBuilderFactory> seriesBuilderFactories;

    private StatementBinderFactory defaultStatementBinderFactory;
    private Map<Integer, StatementBinderFactory> statementBinderFactories;

    private BindingDebugConverter bindingDebugConverter;

    public JdbcConnector(DataSource dataSource) {
        this.dataSource = dataSource;

        this.defaultSeriesBuilderFactory = SeriesBuilderFactory::objectAccum;

        // use primitive converters if the column has no nulls
        this.mandatorySeriesBuilderFactories = new HashMap<>();
        this.mandatorySeriesBuilderFactories.put(Types.BOOLEAN, SeriesBuilderFactory::booleanAccum);
        this.mandatorySeriesBuilderFactories.put(Types.INTEGER, SeriesBuilderFactory::intAccum);
        this.mandatorySeriesBuilderFactories.put(Types.DOUBLE, SeriesBuilderFactory::doubleAccum);
        this.mandatorySeriesBuilderFactories.put(Types.FLOAT, SeriesBuilderFactory::doubleAccum);
        // Types.DECIMAL should presumably be mapped to BigDecimal, so not attempting to map to a primitive double

        this.seriesBuilderFactories = new HashMap<>();
        this.seriesBuilderFactories.put(Types.DATE, SeriesBuilderFactory::dateAccum);
        this.seriesBuilderFactories.put(Types.TIME, SeriesBuilderFactory::timeAccum);
        this.seriesBuilderFactories.put(Types.TIMESTAMP, SeriesBuilderFactory::timestampAccum);

        this.defaultStatementBinderFactory = StatementBinderFactory::objectBinder;
        this.statementBinderFactories = new HashMap<>();
        this.statementBinderFactories.put(Types.DATE, StatementBinderFactory::dateBinder);
        this.statementBinderFactories.put(Types.TIME, StatementBinderFactory::timeBinder);
        this.statementBinderFactories.put(Types.TIMESTAMP, StatementBinderFactory::timestampBinder);

        this.bindingDebugConverter = new BindingDebugConverter();
    }

    public TableSaver tableSaver(String tableName) {
        return new TableSaver(this, tableName);
    }

    public TableLoader tableLoader(String tableName) {
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
    public SqlLoader sqlLoader(String sql) {
        return new SqlLoader(this, sql);
    }

    protected SeriesBuilder<ResultSet, ?> createColumnAccum(int pos, int type, boolean mandatory) {

        SeriesBuilderFactory sbf = null;

        // try to use primitive converters if the column has no nulls
        if (mandatory) {
            sbf = mandatorySeriesBuilderFactories.get(type);
        }

        if (sbf == null) {
            sbf = seriesBuilderFactories.getOrDefault(type, defaultSeriesBuilderFactory);
        }

        return sbf.createAccum(pos);
    }

    protected StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        JdbcConsumer<Object>[] bindings = new JdbcConsumer[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);
            bindings[i] = getStatementBinder(statement, jdbcType, jdbcPos);
        }

        return new StatementBinder(bindings);
    }

    protected BindingDebugConverter getBindingDebugConverter() {
        return bindingDebugConverter;
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

    private JdbcConsumer<Object> getStatementBinder(PreparedStatement statement, int type, int pos) {
        return statementBinderFactories
                .getOrDefault(type, defaultStatementBinderFactory)
                .binder(new StatementPosition(statement, type, pos));
    }
}
