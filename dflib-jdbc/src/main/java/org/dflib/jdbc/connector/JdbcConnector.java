package org.dflib.jdbc.connector;

import org.dflib.Extractor;
import org.dflib.jdbc.connector.metadata.DbMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.statement.ValueConverterFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * An abstraction on top of JDBC DataSource that smoothens a variety of cross-DB portability issues. All DB-aware
 * operations in DFLib use JdbcConnector for access.
 *
 * @since 0.6
 */
public interface JdbcConnector {

    TableSaver tableSaver(String tableName);

    /**
     * @since 0.7
     */
    TableSaver tableSaver(TableFQName tableName);

    TableLoader tableLoader(String tableName);

    /**
     * @since 0.7
     */
    TableLoader tableLoader(TableFQName tableName);

    /**
     * @since 0.8
     */
    TableDeleter tableDeleter(String tableName);

    /**
     * @since 0.8
     */
    TableDeleter tableDeleter(TableFQName tableName);

    /**
     * Creates a new {@link SqlLoader} to load DataFrame from a custom SQL query.
     *
     * @param sql a parameterized SQL statement that should be run to get the DataFrame data. Format of the SQL String
     *            corresponds to the JDBC {@link java.sql.PreparedStatement}. So e.g. it may contain "?" placeholders
     *            for bound parameters. Bound parameter values are then passed via {@link SqlLoader#params(Object...)}.
     * @return a new SqlLoader
     */
    SqlLoader sqlLoader(String sql);

    /**
     * Creates a new {@link SqlSaver} to insert/update DataFrame or Series data with custom SQL.
     *
     * @param sql a parameterized SQL statement. Format of the SQL String should correspond to the JDBC
     *            {@link java.sql.PreparedStatement}. So it may contain "?" placeholders for bound parameters.
     * @return a new {@link SqlSaver}
     * @since 0.8
     */
    SqlSaver sqlSaver(String sql);

    StatementBuilder createStatementBuilder(String sql);

    DbMetadata getMetadata();

    /**
     * Returns Connector's internal DataSource
     *
     * @return Connector's internal DataSource
     * @since 0.7
     */
    DataSource getDataSource();

    Connection getConnection();

    String quoteIdentifier(String bareIdentifier);

    /**
     * Quotes all parts of the fully qualified table name.
     *
     * @since 0.7
     */
    String quoteTableName(TableFQName tableName);

    /**
     * Creates an extractor for a column of the {@link ResultSet} at a given position.
     *
     * @param resultSetPosition 1-based position of the column in the ResultSet.
     * @param type              JDBC type of the value per {@link java.sql.Types}
     * @param mandatory         whether the value is mandatory. This information helps to optimize produced columns,
     *                          using primitive Series for mandatory numeric / boolean columns.
     * @since 0.16
     */
    Extractor<ResultSet, ?> createExtractor(int resultSetPosition, int type, boolean mandatory);

    SqlLogger getSqlLogger();

    ValueConverterFactory getBindConverterFactory();
}
