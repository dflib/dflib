package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.condition.ConditionBuilder;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.sample.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TableLoader {

    protected JdbcConnector connector;
    protected int limit = -1;
    private TableFQName tableName;
    private String[] columns;
    private ConditionBuilder condition;
    private int rowSampleSize;
    private Random rowsSampleRandom;
    private final List<ColConfigurator> colConfigurators;

    public TableLoader(JdbcConnector connector, TableFQName tableName) {
        this.connector = connector;
        this.tableName = tableName;
        this.condition = new ConditionBuilder(connector);
        this.colConfigurators = new ArrayList<>();
    }

    public TableLoader cols(String... columns) {
        this.columns = columns;
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public TableLoader compactCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public TableLoader compactCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures TableLoader to retrieve rows that match rows in the provided "condition" DataFrame. Condition
     * DataFrame must contain columns with names that are present in the DB table. Often it would contain PK columns
     * or otherwise unique columns.
     *
     * @param condition a DataFrame with data that defines load criteria.
     * @return this TableLoader
     */
    public TableLoader eq(DataFrame condition) {
        this.condition.condition(condition, false);
        return this;
    }

    /**
     * Configures TableLoader to retrieve rows that do not match the rows in the provided "condition" DataFrame.
     * Condition DataFrame must contain columns with names that are present in the DB table. Often it would contain
     * PK columns or otherwise unique columns.
     *
     * @param condition a DataFrame with data that defines negated load criteria.
     * @return this TableLoader
     */
    public TableLoader neq(DataFrame condition) {
        this.condition.condition(condition, true);
        return this;
    }

    // TODO: limit without sorting may return unpredictable data.. should we allow to specify a sort column?
    public TableLoader limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#rowsSample(int, Random)}, this method can be used on potentially very large
     * result sets. If you are executing multiple sampling runs in parallel, consider using {@link #rowsSample(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @return this loader instance
     */
    public TableLoader rowsSample(int size) {
        return rowsSample(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures the loader to select a sample of the rows from the ResultSet. Unlike
     * {@link DataFrame#rowsSample(int, Random)}, this method can be used on potentially very large result sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
     */
    public TableLoader rowsSample(int size, Random random) {
        this.rowSampleSize = size;
        this.rowsSampleRandom = Objects.requireNonNull(random);
        return this;
    }

    public DataFrame load() {
        // "no condition" means return all rows; "empty condition" means return no rows
        return condition.noCondition() || condition.nonEmptyCondition()
                ? fetchDataFrame()
                : createEmptyDataFrame();
    }

    protected DataFrame createEmptyDataFrame() {
        String[] columns = useStandardColumns()
                ? connector.getMetadata().getTable(tableName).getColumnNames()
                : this.columns;

        return DataFrame.empty(columns);
    }

    protected DataFrame fetchDataFrame() {
        return new SqlLoader(connector, buildSql())
                .colConfigurators(colConfigurators)
                .limit(limit)
                .rowsSample(rowSampleSize, rowsSampleRandom)
                .load(condition.bindingParams());
    }

    protected String buildSql() {

        // TODO: should "limit"" be translated into the SQL LIMIT clause?
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
        if (condition.nonEmptyCondition()) {
            buffer.append(" where ");
            condition.toSqlCondition(buffer);
        }

        return buffer;
    }
}
