package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.Printers;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.series.builder.SeriesBuilder;
import com.nhl.dflib.jdbc.connector.metadata.DbMetadata;
import com.nhl.dflib.jdbc.connector.statement.ValueConverter;
import com.nhl.dflib.jdbc.connector.statement.ValueConverterFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstraction on top of JDBC DataSource that smoothens a variety of cross-DB portability issues.
 */
public class DefaultJdbcConnector implements JdbcConnector {

    private DataSource dataSource;
    private DbMetadata metadata;
    private IdentifierQuoter quoter;

    private SeriesBuilderFactory defaultSeriesBuilderFactory;
    private Map<Integer, SeriesBuilderFactory> mandatorySeriesBuilderFactories;
    private Map<Integer, SeriesBuilderFactory> seriesBuilderFactories;

    private ValueConverterFactory preBindConverterFactory;
    private SqlLogger sqlLogger;

    public DefaultJdbcConnector(DataSource dataSource, DbMetadata metadata) {
        this.dataSource = dataSource;
        this.metadata = metadata;

        this.defaultSeriesBuilderFactory = SeriesBuilderFactory::objectAccum;

        // use primitive converters if the column has no nulls
        this.mandatorySeriesBuilderFactories = new HashMap<>();
        this.mandatorySeriesBuilderFactories.put(Types.BOOLEAN, SeriesBuilderFactory::booleanAccum);
        this.mandatorySeriesBuilderFactories.put(Types.INTEGER, SeriesBuilderFactory::intAccum);
        this.mandatorySeriesBuilderFactories.put(Types.DOUBLE, SeriesBuilderFactory::doubleAccum);
        this.mandatorySeriesBuilderFactories.put(Types.FLOAT, SeriesBuilderFactory::doubleAccum);
        this.mandatorySeriesBuilderFactories.put(Types.BIGINT, SeriesBuilderFactory::longAccum);

        // Types.DECIMAL should presumably be mapped to BigDecimal, so not attempting to map to a primitive double

        this.seriesBuilderFactories = new HashMap<>();
        this.seriesBuilderFactories.put(Types.DATE, SeriesBuilderFactory::dateAccum);
        this.seriesBuilderFactories.put(Types.TIME, SeriesBuilderFactory::timeAccum);
        this.seriesBuilderFactories.put(Types.TIMESTAMP, SeriesBuilderFactory::timestampAccum);

        this.preBindConverterFactory = createPreBindConverterFactory();

        this.quoter = createQuoter();
        this.sqlLogger = createSqlLogger();
    }

    protected ValueConverterFactory createPreBindConverterFactory() {
        Map<Integer, ValueConverter> converters = new HashMap<>();
        converters.put(Types.DATE, ValueConverter.dateConverter());
        converters.put(Types.TIME, ValueConverter.timeConverter());
        converters.put(Types.TIMESTAMP, ValueConverter.timestampConverter());
        converters.put(Types.INTEGER, ValueConverter.intConverter());
        converters.put(Types.VARCHAR, ValueConverter.stringConverter());

        return new ValueConverterFactory(ValueConverter.defaultConverter(), converters);
    }

    protected SqlLogger createSqlLogger() {
        return new SqlLogger(Printers.inline);
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

    /**
     * Creates a new {@link SqlLoader} to load DataFrame from a custom SQL query.
     *
     * @param sql a parameterized SQL statement that should be run to get the DataFrame data. Format of the SQL String
     *            corresponds to the JDBC {@link java.sql.PreparedStatement}. So e.g. it may contain "?" placeholders
     *            for bound parameters. Bound parameters are then passed via {@link SqlLoader#params(Object...)}.
     * @return a new SqlLoader
     */
    @Override
    public SqlLoader sqlLoader(String sql) {
        return new SqlLoader(this, sql);
    }

    @Override
    public SqlSaver sqlSaver(String sql) {
        return new SqlSaver(this, sql);
    }

    @Override
    public SeriesBuilder<ResultSet, ?> createColumnReader(int pos, int type, boolean mandatory) {

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

    /**
     * @since 0.6
     */
    @Override
    public StatementBuilder createStatementBuilder(String sql) {
        return new StatementBuilder(this).sql(sql);
    }

    /**
     * @since 0.6
     */
    @Override
    public ValueConverterFactory getBindConverterFactory() {
        return preBindConverterFactory;
    }

    /**
     * @since 0.6
     */
    @Override
    public DbMetadata getMetadata() {
        return metadata;
    }

    /**
     * @since 0.6
     */
    @Override
    public SqlLogger getSqlLogger() {
        return sqlLogger;
    }

    /**
     * @since 0.7
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @since 0.6
     */
    @Override
    public Connection getConnection() throws SQLException {

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

    /**
     * @since 0.6
     */
    @Override
    public String quoteIdentifier(String bareIdentifier) {
        return quoter.quoted(bareIdentifier);
    }

    /**
     * @since 0.7
     */
    @Override
    public String quoteTableName(TableFQName tableName) {

        String qtn = quoter.quoted(tableName.getTable());
        if (!tableName.hasCatalog() && !tableName.hasSchema()) {
            return qtn;
        }

        StringBuilder buffer = new StringBuilder();
        if (tableName.hasCatalog()) {
            buffer.append(quoter.quoted(tableName.getCatalog())).append(".");
        }

        if (tableName.hasSchema()) {
            buffer.append(quoter.quoted(tableName.getSchema())).append(".");
        }

        return buffer.append(qtn).toString();
    }

    protected IdentifierQuoter createQuoter() {

        String identifierQuote = getMetadata().getIdentifierQuote();

        // if no quotations are supported, per JDBC spec the returned value is space

        return " ".equals(identifierQuote)
                ? IdentifierQuoter.noQuote()
                : IdentifierQuoter.forQuoteSymbol(identifierQuote);
    }
}
