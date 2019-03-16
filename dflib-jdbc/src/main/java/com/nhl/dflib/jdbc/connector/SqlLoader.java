package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLoader.class);

    protected JdbcConnector connector;
    protected int maxRows;
    private JdbcFunction<Connection, String> sqlProducer;
    private Object[] params;

    public SqlLoader(JdbcConnector connector, String sql) {
        this(connector, c -> sql);
    }

    public SqlLoader(JdbcConnector connector, JdbcFunction<Connection, String> sqlProducer) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
        this.sqlProducer = sqlProducer;
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

        try (Connection c = connector.getConnection()) {

            SelectStatement statement = createStatement(c);

            try (PreparedStatement ps = statement.toJdbcStatement(c)) {

                try (ResultSet rs = ps.executeQuery()) {

                    Index columns = createIndex(rs);
                    List<Object[]> data = loadData(rs);
                    return DataFrame.fromListOfRows(columns, data);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    protected SelectStatement createStatement(Connection connection) throws SQLException {
        String sql = sqlProducer.apply(connection);
        logSql(sql);
        return (params == null || params.length == 0)
                ? new SelectStatementNoParams(sql)
                : new SelectStatementWithParams(sql, params, connector::createBinder);
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

        return Index.withNames(names);
    }

    protected RowReader createRowReader(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int width = rsmd.getColumnCount();
        JdbcFunction<ResultSet, Object>[] readers = new JdbcFunction[width];

        for (int i = 0; i < width; i++) {
            int jdbcPos = i + 1;
            readers[i] = connector.getValueReader(rsmd.getColumnType(jdbcPos), jdbcPos);
        }

        return new RowReader(readers);
    }

    private List<Object[]> loadData(ResultSet rs) throws SQLException {

        RowReader reader = createRowReader(rs);
        List<Object[]> results = new ArrayList<>();
        while (rs.next() && results.size() < maxRows) {
            results.add(reader.readRow(rs));
        }

        return results;
    }
}
