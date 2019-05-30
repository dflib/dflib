package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

public class TableLoader {

    protected JdbcConnector connector;
    protected int maxRows;
    private String tableName;
    private String[] columns;
    private DataFrame matchValues;

    public TableLoader(JdbcConnector connector, String tableName) {
        this.connector = connector;
        this.maxRows = Integer.MAX_VALUE;
        this.tableName = tableName;
    }

    public TableLoader includeColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    /**
     * Configures TableLoader to retrieve data that matches values in the provided DataFrame. Such DataFrame may
     * contain unique key columns of the table.
     *
     * @param matchValues a DataFrame with columns matching one or more table columns, and values that comprise
     *                    table load criteria.
     * @return
     * @since 0.6
     */
    public TableLoader matching(DataFrame matchValues) {
        this.matchValues = matchValues;
        return this;
    }

    // TODO: limit without sorting may return unpredictable data.. should we allow to specify a sort column?
    public TableLoader maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public DataFrame load() {
        return new SqlLoader(connector, buildSql())
                .maxRows(maxRows)
                .params(collectBindingParams())
                .load();
    }

    protected Series<?> collectBindingParams() {
        int criteriaHeight = matchValues != null ? matchValues.height() : 0;
        if (criteriaHeight == 0) {
            return Series.forData();
        }

        int criteriaWidth = matchValues != null ? matchValues.width() : 0;
        switch (criteriaWidth) {
            case 0:
                return Series.forData();
            case 1:
                return matchValues.getColumn(0);
            default:
                // need to build a Series that is concatentaion of DataFrame rows (not columns)
                // Perhaps a specialized Series<Object> "w X h" proxy for a DataFrame?
                throw new UnsupportedOperationException("TODO");
        }
    }

    protected String buildSql() {

        // TODO: should maxRows be translated into the SQL LIMIT clause?
        //  Some DBs have crazy limit syntax, so this may be hard to generalize..

        StringBuilder sql = new StringBuilder("select ");
        appendColumnsSql(sql);
        sql.append(" from ").append(connector.quoteIdentifier(tableName));
        appendWhereSql(sql);

        return sql.toString();
    }

    protected StringBuilder appendColumnsSql(StringBuilder buffer) {

        if (this.columns == null || columns.length == 0) {
            return buffer.append("*");
        }

        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                buffer.append(", ");
            }

            buffer.append(connector.quoteIdentifier(columns[i]));
        }

        return buffer;
    }

    protected StringBuilder appendWhereSql(StringBuilder buffer) {

        int criteriaHeight = matchValues != null ? matchValues.height() : 0;
        if (criteriaHeight == 0) {
            return buffer;
        }

        int criteriaWidth = matchValues != null ? matchValues.width() : 0;
        switch (criteriaWidth) {
            case 0:
                return buffer;
            case 1:
                return appendWhereSql_SingleColumn(
                        buffer,
                        matchValues.getColumnsIndex().getLabel(0),
                        criteriaHeight);
            default:
                return appendWhereSql_MultipleColumns(buffer, criteriaWidth, criteriaHeight);
        }
    }

    protected StringBuilder appendWhereSql_SingleColumn(StringBuilder buffer, String columnName, int criteriaHeight) {

        buffer.append(" where ")
                .append(connector.quoteIdentifier(columnName))
                .append(" in (?");

        for (int i = 1; i < criteriaHeight; i++) {
            buffer.append(", ?");
        }

        return buffer.append(")");
    }

    protected StringBuilder appendWhereSql_MultipleColumns(StringBuilder buffer, int criteriaWidth, int criteriaHeight) {
        throw new UnsupportedOperationException("TODO");
    }
}
