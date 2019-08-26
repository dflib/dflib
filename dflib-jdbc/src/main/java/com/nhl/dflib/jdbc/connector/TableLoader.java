package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.series.ByRowSeries;

import java.util.Objects;
import java.util.Random;

public class TableLoader {

    protected JdbcConnector connector;
    protected int maxRows;
    private TableFQName tableName;
    private String[] columns;
    private DataFrame condition;
    private int rowSampleSize;
    private Random rowsSampleRandom;

    public TableLoader(JdbcConnector connector, TableFQName tableName) {
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

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large
     * result sets. If you are executing multiple sampling runs in parallel, consider using {@link #sampleRows(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @return this loader instance
     * @since 0.7
     */
    public TableLoader sampleRows(int size) {
        return sampleRows(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large result sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
     * @since 0.7
     */
    public TableLoader sampleRows(int size, Random random) {
        this.rowSampleSize = size;
        this.rowsSampleRandom = Objects.requireNonNull(random);
        return this;
    }

    public DataFrame load() {
        // "no condition" means return all rows; "empty condition" means return no rows
        return condition == null || condition.height() > 0
                ? fetchDataFrame()
                : createEmptyDataFrame();
    }

    protected DataFrame createEmptyDataFrame() {
        String[] columns = useStandardColumns()
                ? connector.getMetadata().getTable(tableName).getColumnNames()
                : this.columns;

        return DataFrame.newFrame(columns).empty();
    }

    protected DataFrame fetchDataFrame() {
        return new SqlLoader(connector, buildSql())
                .maxRows(maxRows)
                .sampleRows(rowSampleSize, rowsSampleRandom)
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
        sql.append(" from ").append(connector.quoteTableName(tableName));
        appendWhereSql(sql);

        return sql.toString();
    }

    protected StringBuilder appendColumnsSql(StringBuilder buffer) {

        if (useStandardColumns()) {
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

    protected boolean useStandardColumns() {
        return this.columns == null || columns.length == 0;
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
