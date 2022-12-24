package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.Printers;
import com.nhl.dflib.jdbc.connector.loader.JdbcColumnBuilderFactory;
import com.nhl.dflib.jdbc.connector.loader.JdbcColumnBuilder;
import com.nhl.dflib.jdbc.connector.metadata.DbMetadata;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.jdbc.connector.statement.ValueConverter;
import com.nhl.dflib.jdbc.connector.statement.ValueConverterFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DefaultJdbcConnector implements JdbcConnector {

    private final DataSource dataSource;
    private final DbMetadata metadata;
    private final IdentifierQuoter quoter;

    private final JdbcColumnBuilderFactory defaultColumnBuilderFactory;
    private final Map<Integer, JdbcColumnBuilderFactory> primitiveColumnBuilderFactories;
    private final Map<Integer, JdbcColumnBuilderFactory> columnBuilderFactories;

    private final ValueConverterFactory preBindConverterFactory;
    private final SqlLogger sqlLogger;

    public DefaultJdbcConnector(
            DataSource dataSource,
            DbMetadata metadata,
            Map<Integer, JdbcColumnBuilderFactory> columnBuilderFactories) {

        this.dataSource = dataSource;
        this.metadata = metadata;

        this.defaultColumnBuilderFactory = JdbcColumnBuilderFactory::objectCol;

        // use primitive converters if the column has no nulls
        this.primitiveColumnBuilderFactories = new HashMap<>();
        this.primitiveColumnBuilderFactories.put(Types.BOOLEAN, JdbcColumnBuilderFactory::booleanCol);
        this.primitiveColumnBuilderFactories.put(Types.INTEGER, JdbcColumnBuilderFactory::intCol);
        this.primitiveColumnBuilderFactories.put(Types.DOUBLE, JdbcColumnBuilderFactory::doubleCol);
        this.primitiveColumnBuilderFactories.put(Types.FLOAT, JdbcColumnBuilderFactory::doubleCol);
        this.primitiveColumnBuilderFactories.put(Types.BIGINT, JdbcColumnBuilderFactory::longCol);
        // mysql return bit with code -7 instead boolean -16 type
        this.primitiveColumnBuilderFactories.put(Types.BIT, JdbcColumnBuilderFactory::booleanCol);

        // Types.DECIMAL should presumably be mapped to BigDecimal, so not attempting to map to a primitive double

        this.columnBuilderFactories = columnBuilderFactories;
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
     * @since 0.8
     */
    @Override
    public TableDeleter tableDeleter(TableFQName tableName) {
        return new TableDeleter(this, tableName);
    }

    /**
     * @since 0.8
     */
    @Override
    public TableDeleter tableDeleter(String tableName) {
        return tableDeleter(getMetadata().parseTableName(tableName));
    }

    /**
     * Creates a new {@link SqlLoader} to load DataFrame from a custom SQL query.
     *
     * @param sql a parameterized SQL statement that should be run to get the DataFrame data. Format of the SQL String
     *            corresponds to the JDBC {@link java.sql.PreparedStatement}. So e.g. it may contain "?" placeholders
     *            for bound parameters. Bound parameters are then passed via {@link SqlLoader#load(Object...)}, etc..
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
    public JdbcColumnBuilder<?> createColumnBuilder(int pos, int type, boolean mandatory) {

        JdbcColumnBuilderFactory sbf = null;

        // try to use primitive converters if the column has no nulls
        if (mandatory) {
            sbf = primitiveColumnBuilderFactories.get(type);
        }

        if (sbf == null) {
            sbf = columnBuilderFactories.getOrDefault(type, defaultColumnBuilderFactory);
        }

        return sbf.createBuilder(pos);
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

            throw new RuntimeException("Error setting connection auto commit to 'false'", e);
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
