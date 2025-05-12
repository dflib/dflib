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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Loads DB data from DB as a DataFrame via custom SQL. Instances of this class can be reused for different sets of
 * parameters. This is a more customizable alternative to {@link TableLoader}.
 */
public class SqlLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLoader.class);

    protected final JdbcConnector connector;
    private final String sql;
    private final List<ColConfigurator> colConfigurators;

    protected int limit;
    private int rowSampleSize;
    private Random rowsSampleRandom;

    public SqlLoader(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.limit = Integer.MAX_VALUE;
        this.sql = sql;
        this.colConfigurators = new ArrayList<>();
    }

    protected SqlLoader copy() {
        SqlLoader copy = new SqlLoader(connector, sql);
        copy.limit = this.limit;
        copy.rowSampleSize = this.rowSampleSize;
        copy.rowsSampleRandom = this.rowsSampleRandom;
        copy.colConfigurators.addAll(this.colConfigurators);
        return copy;
    }

    // non-public method for the sake of TableLoader
    SqlLoader colConfigurators(List<ColConfigurator> configurators) {
        colConfigurators.addAll(configurators);
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public SqlLoader compactCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public SqlLoader compactCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    public SqlLoader limit(int limit) {
        if (this.limit == limit) {
            return this;
        }

        SqlLoader copy = copy();
        copy.limit = limit;
        return copy;
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#rowsSample(int, Random)}, this method can be used on potentially very large
     * result sets. If you are executing multiple sampling runs in parallel, consider using {@link #rowsSample(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @return this loader instance
     */
    public SqlLoader rowsSample(int size) {
        return rowsSample(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#rowsSample(int, Random)}, this method can be used on potentially very large result sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
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

    public DataFrame load(Object... params) {
        return load(Series.of(params));
    }

    public DataFrame load(Series<?> params) {

        LOGGER.debug("loading DataFrame...");

        // TODO: should "limit" be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

        return connector
                .createStatementBuilder(sql)
                .bind(params)
                .select(this::loadDataFrame);
    }

    protected DataFrame loadDataFrame(ResultSet rs) throws SQLException {
        Index index = createIndex(rs);

        Extractor<ResultSet, ?>[] extractors = extractors(index, rs);

        DataFrameAppender<ResultSet> appender = DataFrame
                .byRow(extractors)
                .columnIndex(index)
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

        return Index.ofDeduplicated(names);
    }

    protected Extractor<ResultSet, ?>[] extractors(Index index, ResultSet resultSet) throws SQLException {

        Map<Integer, ColConfigurator> configurators = new HashMap<>();
        for (ColConfigurator c : colConfigurators) {
            // later configs override earlier configs at the same position
            configurators.put(c.srcPos(index), c);
        }

        ResultSetMetaData schema = resultSet.getMetaData();
        int w = schema.getColumnCount();
        Extractor<ResultSet, ?>[] extractors = new Extractor[w];

        for (int i = 0; i < w; i++) {
            ColConfigurator cc = configurators.computeIfAbsent(i, ii -> ColConfigurator.objectCol(ii, false));
            extractors[i] = cc.extractor(i, connector, schema);
        }

        return extractors;
    }
}
