package org.dflib.jdbc.connector.condition;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.jdbc.connector.JdbcConnector;
import org.dflib.series.ByRowSeries;

import java.util.Objects;

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
                        condition.getColumnsIndex().get(0),
                        criteriaHeight);
            default:
                return appendWhereSql_MultiColumns(buffer, condition.getColumnsIndex(), criteriaHeight);
        }
    }

    public Series<?> bindingParams() {
        int criteriaHeight = condition != null ? condition.height() : 0;
        if (criteriaHeight == 0) {
            return Series.of();
        }

        int criteriaWidth = condition != null ? condition.width() : 0;
        switch (criteriaWidth) {
            case 0:
                return Series.of();
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
            columns[i] = connector.quoteIdentifier(columnsIndex.get(i));
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
