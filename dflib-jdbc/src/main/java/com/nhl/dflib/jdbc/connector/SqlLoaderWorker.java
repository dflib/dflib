package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.SeriesBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

class SqlLoaderWorker {

    private Index columns;
    protected SeriesBuilder<ResultSet, ?>[] accumulators;
    protected int maxRows;

    public SqlLoaderWorker(Index columns, SeriesBuilder<ResultSet, ?>[] accumulators, int maxRows) {
        this.columns = columns;
        this.maxRows = maxRows;
        this.accumulators = accumulators;
    }

    DataFrame load(ResultSet rs) throws SQLException {
        consumeResultSet(rs);
        return toDataFrame();
    }

    protected void consumeResultSet(ResultSet rs) throws SQLException {

        int w = accumulators.length;
        int size = 0;

        while (rs.next() && size++ < maxRows) {
            addRow(w, rs);
        }
    }

    protected DataFrame toDataFrame() {
        int width = columns.size();
        Series<?>[] series = new Series[width];
        for (int i = 0; i < width; i++) {
            series[i] = accumulators[i].toSeries();
        }

        return DataFrame.newFrame(columns).columns(series);
    }

    protected void addRow(int width, ResultSet resultSet) {
        for (int i = 0; i < width; i++) {
            accumulators[i].add(resultSet);
        }
    }
}
