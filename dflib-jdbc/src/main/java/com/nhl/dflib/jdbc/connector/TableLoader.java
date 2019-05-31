package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ByRowSeries;

public class TableLoader {

    protected JdbcConnector connector;
    protected int maxRows;
    private String tableName;
    private String[] columns;
    private DataFrame condition;

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
     * Configures TableLoader to retrieve rows that match rows in the provided "condition" DataFrame. Condition
     * DataFrame must contain columns with names that are present in the DB table. Often it would contain PK columns
     * or otherwise unique columns.
     *
     * @param condition a DataFrame with data that defines load criteria.
     * @return this TableLoader
     * @since 0.6
     */
    public TableLoader eq(DataFrame condition) {
        this.condition = condition;
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
        int criteriaHeight = condition != null ? condition.height() : 0;
        if (criteriaHeight == 0) {
            return Series.forData();
        }

        int criteriaWidth = condition != null ? condition.width() : 0;
        switch (criteriaWidth) {
            case 0:
                return Series.forData();
            case 1:
                return condition.getColumn(0);
            default:
                return new ByRowSeries<>(condition);
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

        int criteriaHeight = condition != null ? condition.height() : 0;
        if (criteriaHeight == 0) {
            return buffer;
        }

        int criteriaWidth = condition != null ? condition.width() : 0;
        switch (criteriaWidth) {
            case 0:
                return buffer;
            case 1:
                return appendWhereSql_SingleColumn(
                        buffer,
                        condition.getColumnsIndex().getLabel(0),
                        criteriaHeight);
            default:
                return appendWhereSql_MultiColumns(buffer, condition.getColumnsIndex(), criteriaHeight);
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

    protected StringBuilder appendWhereSql_MultiColumns(StringBuilder buffer, Index columnsIndex, int criteriaHeight) {

        String part = singleMultiColumnCondition(columnsIndex);

        buffer.append(" where ").append(part);

        for (int i = 1; i < criteriaHeight; i++) {
            buffer.append(" or ").append(part);
        }

        return buffer;
    }

    private String singleMultiColumnCondition(Index columnsIndex) {
        int w = columnsIndex.size();
        String[] columns = new String[w];
        for (int i = 0; i < w; i++) {
            columns[i] = connector.quoteIdentifier(columnsIndex.getLabel(i));
        }

        StringBuilder buffer = new StringBuilder();

        buffer.append("(");
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                buffer.append(" and ");
            }
            buffer.append(columns[i]).append(" = ?");
        }

        buffer.append(")");

        return buffer.toString();
    }
}
