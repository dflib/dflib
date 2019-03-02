package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
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

    private JdbcSupplier<Connection> connectionProvider;
    private JdbcFunction<Connection, SqlStatement> statementFactory;
    private JdbcFunction<ResultSet, Index> indexFactory;
    private JdbcFunction<ResultSet, RowReader> rowReaderFactory;
    private int maxRows;

    public JdbcSelector(
            JdbcSupplier<Connection> connectionProvider,
            JdbcFunction<Connection, SqlStatement> statementFactory,
            JdbcFunction<ResultSet, Index> indexFactory,
            JdbcFunction<ResultSet, RowReader> rowReaderFactory,
            int maxRows
    ) {

        this.connectionProvider = connectionProvider;
        this.statementFactory = statementFactory;
        this.indexFactory = indexFactory;
        this.rowReaderFactory = rowReaderFactory;
        this.maxRows = maxRows;
    }

    public DataFrame load() {

        try (Connection c = connectionProvider.get()) {

            SqlStatement sql = statementFactory.apply(c);
            LOGGER.debug("Loading DataFrame with SQL: {}", sql);

            // TODO: log bindings

            try (PreparedStatement st = sql.toJdbcStatement(c)) {

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
