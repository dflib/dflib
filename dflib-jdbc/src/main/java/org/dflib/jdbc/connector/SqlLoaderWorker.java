package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.builder.DataFrameAppender;

import java.sql.ResultSet;
import java.sql.SQLException;

class SqlLoaderWorker {

    protected final DataFrameAppender<ResultSet> appender;
    protected final int limit;

    public SqlLoaderWorker(DataFrameAppender<ResultSet> appender, int limit) {
        this.limit = limit < 0 ? Integer.MAX_VALUE : limit;
        this.appender = appender;
    }

    DataFrame load(ResultSet rs) throws SQLException {
        consumeResultSet(rs);
        return toDataFrame();
    }

    protected void consumeResultSet(ResultSet rs) throws SQLException {

        int size = 0;

        while (rs.next() && size++ < limit) {
            appender.append(rs);
        }
    }

    protected DataFrame toDataFrame() {
        return appender.toDataFrame();
    }
}
