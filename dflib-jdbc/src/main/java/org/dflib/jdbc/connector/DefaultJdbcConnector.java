package org.dflib.jdbc.connector;

import org.dflib.Extractor;
import org.dflib.Printers;
import org.dflib.jdbc.connector.loader.JdbcExtractorFactory;
import org.dflib.jdbc.connector.metadata.DbMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.statement.ValueConverter;
import org.dflib.jdbc.connector.statement.ValueConverterFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DefaultJdbcConnector implements JdbcConnector {

    private final DataSource dataSource;
    private final DbMetadata metadata;
    private final IdentifierQuoter quoter;

    private final JdbcExtractorFactory defaultExtractorFactory;
    private final Map<Integer, JdbcExtractorFactory> primitiveExtractorFactories;
    private final Map<Integer, JdbcExtractorFactory> extractorFactories;

    private final ValueConverterFactory preBindConverterFactory;
    private final SqlLogger sqlLogger;

    public DefaultJdbcConnector(
            DataSource dataSource,
            DbMetadata metadata,
            Map<Integer, JdbcExtractorFactory> extractorFactories) {

        this.dataSource = dataSource;
        this.metadata = metadata;

        this.defaultExtractorFactory = JdbcExtractorFactory::$col;

        // use primitive converters if the column has no nulls
        this.primitiveExtractorFactories = new HashMap<>();
        this.primitiveExtractorFactories.put(Types.BOOLEAN, JdbcExtractorFactory::$bool);
        this.primitiveExtractorFactories.put(Types.INTEGER, JdbcExtractorFactory::$int);
        this.primitiveExtractorFactories.put(Types.DOUBLE, JdbcExtractorFactory::$double);
        this.primitiveExtractorFactories.put(Types.FLOAT, JdbcExtractorFactory::$double);
        this.primitiveExtractorFactories.put(Types.BIGINT, JdbcExtractorFactory::$long);
        // mysql return bit with code -7 instead boolean -16 type
        this.primitiveExtractorFactories.put(Types.BIT, JdbcExtractorFactory::$bool);

        // Types.DECIMAL should presumably be mapped to BigDecimal, so not attempting to map to a primitive double

        this.extractorFactories = extractorFactories;
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
        // Ignoring Environment printer, as SQL params always need to be printed inline
        return new SqlLogger(Printers.inline);
    }

    @Override
    public TableSaver tableSaver(String tableName) {
        return tableSaver(getMetadata().parseTableName(tableName));
    }


    @Override
    public TableSaver tableSaver(TableFQName tableName) {
        return new TableSaver(this, tableName);
    }

    @Override
    public TableLoader tableLoader(String tableName) {
        return tableLoader(getMetadata().parseTableName(tableName));
    }


    @Override
    public TableLoader tableLoader(TableFQName tableName) {
        return new TableLoader(this, tableName);
    }

    @Override
    public TableDeleter tableDeleter(TableFQName tableName) {
        return new TableDeleter(this, tableName);
    }

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
    public Extractor<ResultSet, ?> createExtractor(int resultSetPosition, int type, boolean mandatory) {

        JdbcExtractorFactory factory = null;

        // try to use primitive converters if the column has no nulls
        if (mandatory) {
            factory = primitiveExtractorFactories.get(type);
        }

        if (factory == null) {
            factory = extractorFactories.getOrDefault(type, defaultExtractorFactory);
        }

        return factory.createExtractor(resultSetPosition);
    }

    @Override
    public StatementBuilder createStatementBuilder(String sql) {
        return new StatementBuilder(this).sql(sql);
    }

    @Override
    public ValueConverterFactory getBindConverterFactory() {
        return preBindConverterFactory;
    }

    @Override
    public DbMetadata getMetadata() {
        return metadata;
    }

    @Override
    public SqlLogger getSqlLogger() {
        return sqlLogger;
    }


    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

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

    @Override
    public String quoteIdentifier(String bareIdentifier) {
        return quoter.quoted(bareIdentifier);
    }


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
