package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.builder.DataFrameAppender;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.sample.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Random;

/**
 * Loads DB data from DB as a DataFrame via custom SQL. Instances of this class can be reused for different sets of
 * parameters. This is a more customizable alternative to {@link TableLoader}.
 */
public class SqlLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLoader.class);

    protected final JdbcConnector connector;
    private final String sql;

    protected int limit;
    private int rowSampleSize;
    private Random rowsSampleRandom;

    public SqlLoader(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.limit = Integer.MAX_VALUE;
        this.sql = sql;
    }

    protected SqlLoader copy() {
        SqlLoader copy = new SqlLoader(connector, sql);
        copy.limit = this.limit;
        copy.rowSampleSize = this.rowSampleSize;
        copy.rowsSampleRandom = this.rowsSampleRandom;
        return copy;
    }

    /**
     * @since 1.0.0-M20
     */
    public SqlLoader limit(int maxRows) {
        if (this.limit == maxRows) {
            return this;
        }

        SqlLoader copy = copy();
        copy.limit = maxRows;
        return copy;
    }

    /**
     * @deprecated in favor of {@link #limit(int)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public SqlLoader maxRows(int maxRows) {
        return limit(maxRows);
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large
     * result sets. If you are executing multiple sampling runs in parallel, consider using {@link #rowsSample(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public SqlLoader rowsSample(int size) {
        return rowsSample(size, Sampler.getDefaultRandom());
    }

    /**
     * @deprecated in facfor of {@link #rowsSample(int)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public SqlLoader sampleRows(int size) {
        return rowsSample(size);
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large result sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public SqlLoader rowsSample(int size, Random random) {

        if (this.rowSampleSize == size && this.rowsSampleRandom == random) {
            return this;
        }

        SqlLoader copy = copy();
        copy.rowSampleSize = size;
        copy.rowsSampleRandom = random;
        return copy;
    }

    /**
     * @deprecated in favor of {@link #rowsSample(int, Random)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public SqlLoader sampleRows(int size, Random random) {
        return rowsSample(size, random);
    }

    /**
     * @since 0.8
     */
    public DataFrame load(Object... params) {
        return load(Series.of(params));
    }

    /**
     * @since 0.8
     */
    public DataFrame load(Series<?> params) {

        LOGGER.debug("loading DataFrame...");

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

        return connector
                .createStatementBuilder(sql)
                .bind(params)
                .select(this::loadDataFrame);
    }

    protected DataFrame loadDataFrame(ResultSet rs) throws SQLException {
        Index columns = createIndex(rs);

        Extractor<ResultSet, ?>[] extractors = createExtractors(rs);

        DataFrameAppender<ResultSet> appender = DataFrame
                .byRow(extractors)
                .columnIndex(columns)
                .capacity(rowSampleSize > 0 ? rowSampleSize : 100)
                .sampleRows(rowSampleSize, rowsSampleRandom)
                .appender();

        return new SqlLoaderWorker(appender, limit).load(rs);
    }

    protected Index createIndex(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int width = rsmd.getColumnCount();
        String[] names = new String[width];

        for (int i = 0; i < width; i++) {
            names[i] = rsmd.getColumnLabel(i + 1);
        }

        return Index.of(names);
    }

    protected Extractor<ResultSet, ?>[] createExtractors(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int w = rsmd.getColumnCount();
        Extractor<ResultSet, ?>[] extractors = new Extractor[w];

        for (int i = 0; i < w; i++) {
            int jdbcPos = i + 1;
            extractors[i] = connector.createExtractor(
                    jdbcPos,
                    rsmd.getColumnType(jdbcPos),
                    rsmd.isNullable(jdbcPos) == ResultSetMetaData.columnNoNulls);
        }

        return extractors;
    }
}
