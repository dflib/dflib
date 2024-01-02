package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.builder.DataFrameAppender;

import java.sql.ResultSet;
import java.sql.SQLException;

class SqlLoaderWorker {

    protected final DataFrameAppender<ResultSet> appender;
    protected final int maxRows;

    public SqlLoaderWorker(DataFrameAppender<ResultSet> appender, int maxRows) {
        this.maxRows = maxRows;
        this.appender = appender;
    }

    DataFrame load(ResultSet rs) throws SQLException {
        consumeResultSet(rs);
        return toDataFrame();
    }

    protected void consumeResultSet(ResultSet rs) throws SQLException {

        int size = 0;

        while (rs.next() && size++ < maxRows) {
            appender.append(rs);
        }
    }

    protected DataFrame toDataFrame() {
        return appender.toDataFrame();
    }
}
