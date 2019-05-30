package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.SeriesBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SqlLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLoader.class);

    protected JdbcConnector connector;
    protected int maxRows;
    private String sql;
    private Object[] params;

    public SqlLoader(JdbcConnector connector, String sql) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
        this.sql = sql;
    }

    public SqlLoader params(Object... params) {
        this.params = params;
        return this;
    }

    public SqlLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public DataFrame load() {

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

        SelectStatement statement = createStatement();

        try (Connection c = connector.getConnection()) {

            try (PreparedStatement ps = statement.toJdbcStatement(c)) {

                try (ResultSet rs = ps.executeQuery()) {

                    Index columns = createIndex(rs);
                    Series<?>[] data = loadData(rs);
                    return DataFrame.forColumns(columns, data);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    protected SelectStatement createStatement() {
        logSql(sql);
        return (params == null || params.length == 0)
                ? new SelectStatementNoParams(sql)
                : new SelectStatementWithParams(sql, params, connector.getBinderFactory());
    }

    protected void logSql(String sql) {
        if (LOGGER.isInfoEnabled()) {

            StringBuilder log = new StringBuilder("Loading DataFrame... ").append(sql);

            if (params != null && params.length > 0) {

                BindingDebugConverter paramConverter = connector.getBindingDebugConverter();

                log.append(" [");
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) {
                        log.append(", ");
                    }

                    log.append(paramConverter.convert(params[i]));
                }

                log.append("]");
            }

            LOGGER.info(log.toString());
        }
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
                accums[i].append(rs);
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
