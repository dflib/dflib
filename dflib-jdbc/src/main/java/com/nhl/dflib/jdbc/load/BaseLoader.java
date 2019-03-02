package com.nhl.dflib.jdbc.load;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcOperation;
import com.nhl.dflib.jdbc.select.JdbcSelector;
import com.nhl.dflib.jdbc.select.RowReader;
import com.nhl.dflib.jdbc.select.StatementBinder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract class BaseLoader {

    protected JdbcConnector connector;
    protected int maxRows;

    public BaseLoader(JdbcConnector connector) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
    }

    public DataFrame load() {

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..
        return new JdbcSelector(
                connector,
                this::buildSql,
                this::createIndex,
                this::createRowReader,
                createBinder(),
                maxRows).load();
    }

    protected abstract String buildSql(Connection connection);

    protected abstract StatementBinder createBinder();

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
        JdbcOperation<ResultSet, Object>[] readers = new JdbcOperation[width];

        for (int i = 0; i < width; i++) {
            int jdbcPos = i + 1;
            readers[i] = connector.getValueReader(rsmd.getColumnType(jdbcPos), jdbcPos);
        }

        return new RowReader(readers);
    }
}
