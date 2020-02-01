package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.series.builder.SeriesBuilder;
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

    protected JdbcConnector connector;
    protected int maxRows;
    private String sql;
    private int rowSampleSize;
    private Random rowsSampleRandom;

    public SqlLoader(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
        this.sql = sql;
    }

    protected SqlLoader copy() {
        SqlLoader copy = new SqlLoader(connector, sql);
        copy.maxRows = this.maxRows;
        copy.rowSampleSize = this.rowSampleSize;
        copy.rowsSampleRandom = this.rowsSampleRandom;
        return copy;
    }

    public SqlLoader maxRows(int maxRows) {
        if (this.maxRows == maxRows) {
            return this;
        }

        SqlLoader copy = copy();
        copy.maxRows = maxRows;
        return copy;
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large
     * result sets. If you are executing multiple sampling runs in parallel, consider using {@link #sampleRows(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @return this loader instance
     * @since 0.7
     */
    public SqlLoader sampleRows(int size) {
        return sampleRows(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large result sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
     * @since 0.7
     */
    public SqlLoader sampleRows(int size, Random random) {

        if (this.rowSampleSize == size && this.rowsSampleRandom == random) {
            return this;
        }

        SqlLoader copy = copy();
        copy.rowSampleSize = size;
        copy.rowsSampleRandom = random;
        return copy;
    }

    /**
     * @since 0.8
     */
    public DataFrame load(Object... params) {
        return load(Series.forData(params));
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

        SeriesBuilder<ResultSet, ?>[] accumulators = createAccummulators(rs);

        SqlLoaderWorker worker = rowSampleSize > 0
                ? new SamplingSqlLoaderWorker(columns, accumulators, maxRows, rowSampleSize, rowsSampleRandom)
                : new SqlLoaderWorker(columns, accumulators, maxRows);

        return worker.load(rs);
    }

    protected Index createIndex(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int width = rsmd.getColumnCount();
        String[] names = new String[width];

        for (int i = 0; i < width; i++) {
            names[i] = rsmd.getColumnLabel(i + 1);
        }

        return Index.forLabels(names);
    }

    protected SeriesBuilder<ResultSet, ?>[] createAccummulators(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int w = rsmd.getColumnCount();
        SeriesBuilder<ResultSet, ?>[] accums = new SeriesBuilder[w];

        for (int i = 0; i < w; i++) {
            int jdbcPos = i + 1;
            accums[i] = connector.createColumnReader(
                    jdbcPos,
                    rsmd.getColumnType(jdbcPos),
                    rsmd.isNullable(jdbcPos) == ResultSetMetaData.columnNoNulls);
        }

        return accums;
    }
}
