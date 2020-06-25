package com.nhl.dflib.jdbc.connector.condition;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.series.ByRowSeries;

import java.util.Objects;

/**
 * @since 0.8
 */
public class ConditionBuilder {

    private JdbcConnector connector;
    private DataFrame condition;
    private boolean negateCondition;

    public ConditionBuilder(JdbcConnector connector) {
        this.connector = connector;
    }

    public ConditionBuilder condition(DataFrame condition, boolean negate) {
        this.condition = Objects.requireNonNull(condition);
        this.negateCondition = negate;
        return this;
    }

    public boolean noCondition() {
        return condition == null;
    }

    public boolean nonEmptyCondition() {
        return condition != null && condition.height() > 0;
    }

    public boolean negatedCondition() {
        return negateCondition;
    }

    public StringBuilder toSqlCondition(StringBuilder buffer) {
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

    public Series<?> bindingParams() {
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
                return new ByRowSeries(condition);
        }
    }

    protected StringBuilder appendWhereSql_SingleColumn(StringBuilder buffer, String columnName, int criteriaHeight) {

        buffer.append(connector.quoteIdentifier(columnName))
                .append(negateCondition ? " not in" : " in")
                .append(" (?");

        for (int i = 1; i < criteriaHeight; i++) {
            buffer.append(", ?");
        }

        return buffer.append(")");
    }

    protected StringBuilder appendWhereSql_MultiColumns(StringBuilder buffer, Index columnsIndex, int criteriaHeight) {

        String part = singleMultiColumnCondition(columnsIndex);

        buffer.append(negateCondition ? " not (" : "").append(part);

        for (int i = 1; i < criteriaHeight; i++) {
            buffer.append(" or ").append(part);
        }

        buffer.append(negateCondition ? ")" : "");
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
