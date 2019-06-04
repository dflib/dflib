package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.seriesbuilder.SeriesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SqlLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLoader.class);

    protected JdbcConnector connector;
    protected int maxRows;
    private String sql;
    private Series<?> params;

    public SqlLoader(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
        this.sql = sql;
    }

    public SqlLoader params(Object... params) {
        return params(Series.forData(params));
    }

    /**
     * @since 0.6
     */
    public SqlLoader params(Series<?> params) {
        this.params = params;
        return this;
    }

    public SqlLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public DataFrame load() {

        LOGGER.info("loading DataFrame...");


        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

        return connector
                .createStatementBuilder(sql)
                .bind(params)
                .select(this::loadDataFrame);
    }

    protected DataFrame loadDataFrame(ResultSet rs) throws SQLException {
        Index columns = createIndex(rs);
        Series<?>[] data = loadData(rs);
        return DataFrame.forColumns(columns, data);
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

    protected Series<?>[] loadData(ResultSet rs) throws SQLException {

        SeriesBuilder<ResultSet, ?>[] accums = createAccums(rs);

        int w = accums.length;
        int size = 0;

        while (rs.next() && size++ < maxRows) {
            for (int i = 0; i < w; i++) {
                accums[i].add(rs);
            }
        }

        Series<?>[] series = new Series[w];
        for (int i = 0; i < w; i++) {
            series[i] = accums[i].toSeries();
        }

        return series;
    }

    protected SeriesBuilder<ResultSet, ?>[] createAccums(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int w = rsmd.getColumnCount();
        SeriesBuilder<ResultSet, ?>[] accums = new SeriesBuilder[w];

        for (int i = 0; i < w; i++) {
            int jdbcPos = i + 1;
            accums[i] = connector.createColumnAccum(
                    jdbcPos,
                    rsmd.getColumnType(jdbcPos),
                    rsmd.isNullable(jdbcPos) == ResultSetMetaData.columnNoNulls);
        }

        return accums;
    }
}
