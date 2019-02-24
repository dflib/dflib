package com.nhl.dflib.jdbc.select;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcOperation;
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
    private JdbcOperation<Connection, String> sqlFactory;
    private JdbcOperation<ResultSet, Index> indexFactory;
    private JdbcOperation<ResultSet, RowReader> rowReaderFactory;
    private int maxRows;

    public JdbcSelector(
            JdbcConnector connector,
            JdbcOperation<Connection, String> sqlFactory,
            JdbcOperation<ResultSet, Index> indexFactory,
            JdbcOperation<ResultSet, RowReader> rowReaderFactory,
            int maxRows) {

        this.connector = connector;
        this.sqlFactory = sqlFactory;
        this.indexFactory = indexFactory;
        this.rowReaderFactory = rowReaderFactory;
        this.maxRows = maxRows;
    }

    public DataFrame load() {

        try (Connection c = connector.getConnection()) {

            String sql = sqlFactory.exec(c);
            LOGGER.info("Loading DataFrame with SQL: {}", sql);

            try (PreparedStatement st = c.prepareStatement(sql)) {
                try (ResultSet rs = st.executeQuery()) {

                    Index columns = indexFactory.exec(rs);
                    List<Object[]> data = loadData(rs);
                    return DataFrame.fromRowsList(columns, data);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    private List<Object[]> loadData(ResultSet rs) throws SQLException {

        RowReader reader = rowReaderFactory.exec(rs);
        List<Object[]> results = new ArrayList<>();
        while (rs.next() && results.size() < maxRows) {
            results.add(reader.readRow(rs));
        }

        return results;
    }
}
