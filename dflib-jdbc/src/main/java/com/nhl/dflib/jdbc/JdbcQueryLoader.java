package com.nhl.dflib.jdbc;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.RowReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JdbcQueryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcQueryLoader.class);

    private JdbcConnector connector;
    private Function<Connection, String> sqlGenerator;

    public JdbcQueryLoader(JdbcConnector connector, Function<Connection, String> sqlGenerator) {
        this.connector = connector;
        this.sqlGenerator = sqlGenerator;
    }

    public DataFrame load() {
        try {
            return loadWithExceptions();
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    private DataFrame loadWithExceptions() throws SQLException {

        try (Connection c = connector.getConnection()) {

            String sql = sqlGenerator.apply(c);
            LOGGER.info("Load DataFrame for SQL: {}", sql);

            try (PreparedStatement st = c.prepareStatement(sql)) {
                try (ResultSet rs = st.executeQuery()) {

                    Index columns = loadColumns(rs.getMetaData());
                    List<Object[]> data = loadData(rs, RowReader.arrayReader(columns.size()));
                    return DataFrame.fromRowsList(columns, data);
                }
            }
        }
    }

    private Index loadColumns(ResultSetMetaData rsmd) throws SQLException {

        int count = rsmd.getColumnCount();
        String[] names = new String[count];

        for (int i = 0; i < count; i++) {
            names[i] = rsmd.getColumnLabel(i + 1);
        }

        return Index.withNames(names);
    }

    private List<Object[]> loadData(ResultSet rs, RowReader<Object[]> reader) throws SQLException {

        List<Object[]> result = new ArrayList<>();
        while (rs.next()) {
            result.add(reader.readRow(rs));
        }

        return result;
    }
}
