package com.nhl.dflib.jdbc.select;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSelector.class);

    private JdbcConnector connector;
    private JdbcFunction<Connection, String> sqlFactory;
    private JdbcFunction<ResultSet, Index> indexFactory;
    private JdbcFunction<PreparedStatement, StatementBinder> binder;
    private JdbcFunction<ResultSet, RowReader> rowReaderFactory;
    private int maxRows;

    public JdbcSelector(
            JdbcConnector connector,
            JdbcFunction<Connection, String> sqlFactory,
            JdbcFunction<ResultSet, Index> indexFactory,
            JdbcFunction<PreparedStatement, StatementBinder> binder,
            JdbcFunction<ResultSet, RowReader> rowReaderFactory,
            int maxRows
    ) {

        this.connector = connector;
        this.sqlFactory = sqlFactory;
        this.indexFactory = indexFactory;
        this.rowReaderFactory = rowReaderFactory;
        this.binder = binder;
        this.maxRows = maxRows;
    }

    public DataFrame load() {

        try (Connection c = connector.getConnection()) {

            String sql = sqlFactory.apply(c);
            LOGGER.info("Loading DataFrame with SQL: {}", sql);

            try (PreparedStatement st = c.prepareStatement(sql)) {

                binder.apply(st).bind(st);

                try (ResultSet rs = st.executeQuery()) {

                    Index columns = indexFactory.apply(rs);
                    List<Object[]> data = loadData(rs);
                    return DataFrame.fromRowsList(columns, data);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    private List<Object[]> loadData(ResultSet rs) throws SQLException {

        RowReader reader = rowReaderFactory.apply(rs);
        List<Object[]> results = new ArrayList<>();
        while (rs.next() && results.size() < maxRows) {
            results.add(reader.readRow(rs));
        }

        return results;
    }
}
