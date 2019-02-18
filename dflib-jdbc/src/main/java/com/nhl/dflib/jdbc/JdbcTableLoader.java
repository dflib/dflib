package com.nhl.dflib.jdbc;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class JdbcTableLoader {

    private DataSource dataSource;
    private String tableName;
    private int maxRows;
    private String identifierQuote;


    public JdbcTableLoader(DataSource dataSource, String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
    }

    public JdbcTableLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public DataFrame load() {
        return load(buildSql());
    }

    protected String buildSql() {

        String name = identifierQuote != null ? identifierQuote + tableName + identifierQuote : tableName;

        return "select * from " + tableName;
    }

    protected DataFrame load(String sql) {
        try (Connection c = dataSource.getConnection()) {
            return load(c, sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    protected DataFrame load(Connection c, String sql) throws SQLException {

        try (PreparedStatement st = c.prepareStatement(sql)) {

            try (ResultSet rs = st.executeQuery()) {

                Index columns = loadColumns(rs.getMetaData());
                List<Object[]> data = loadData(rs);
                return DataFrame.fromRowsList(columns, data);
            }
        }
    }

    protected Index loadColumns(ResultSetMetaData rsmd) throws SQLException {

        int count = rsmd.getColumnCount();
        String[] names = new String[count];

        for (int i = 0; i < count; i++) {
            names[i] = rsmd.getColumnLabel(i + 1);
        }

        return Index.withNames(names);
    }

    protected List<Object[]> loadData(ResultSet rs) {
        return Collections.emptyList();
    }

}
