package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.condition.ConditionBuilder;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class TableDeleter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableDeleter.class);

    protected JdbcConnector connector;
    private TableFQName tableName;
    private ConditionBuilder condition;

    public TableDeleter(JdbcConnector connector, TableFQName tableName) {
        this.connector = Objects.requireNonNull(connector);
        this.tableName = Objects.requireNonNull(tableName);
        this.condition = new ConditionBuilder(connector);
    }

    public TableDeleter eq(DataFrame condition) {
        this.condition.condition(condition, false);
        return this;
    }

    public TableDeleter neq(DataFrame condition) {
        this.condition.condition(condition, true);
        return this;
    }

    public int delete() {
        if (condition.noCondition()) {
            return deleteAll();
        } else if (condition.nonEmptyCondition()) {
            return deleteConditional();
        } else {
            return deleteEmptyCondition();
        }
    }

    protected int deleteAll() {
        LOGGER.debug("deleting all rows in '{}'", tableName);

        String sql = createUnqualifiedDeleteStatement(new StringBuilder()).toString();
        return new SqlSaver(connector, sql).save();
    }

    protected int deleteConditional() {
        LOGGER.debug("deleting rows in '{}' {}matching DataFrame...", tableName, condition.negatedCondition() ? "not " : " ");

        String sql = createDeleteStatement(new StringBuilder()).toString();
        return new SqlSaver(connector, sql).save(condition.bindingParams());
    }

    protected int deleteEmptyCondition() {
        return condition.negatedCondition() ? deleteAll() : 0;
    }

    protected StringBuilder createUnqualifiedDeleteStatement(StringBuilder buffer) {
        return buffer.append("delete from " + connector.quoteTableName(tableName));
    }

    protected StringBuilder createDeleteStatement(StringBuilder buffer) {
        createUnqualifiedDeleteStatement(buffer).append(" where ");
        condition.toSqlCondition(buffer);
        return buffer;
    }
}
