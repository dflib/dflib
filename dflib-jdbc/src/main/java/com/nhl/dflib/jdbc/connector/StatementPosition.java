package com.nhl.dflib.jdbc.connector;

import java.sql.PreparedStatement;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class StatementPosition {

    private PreparedStatement statement;
    private int type;
    private int position;
    private Function<Object, Object> valueConverter;

    public StatementPosition(PreparedStatement statement, int type, int position) {
        this(statement, type, position, UnaryOperator.identity());
    }

    public StatementPosition(
            PreparedStatement statement,
            int type,
            int position,
            Function<Object, Object> valueConverter) {

        this.statement = statement;
        this.type = type;
        this.position = position;
        this.valueConverter = valueConverter;
    }

    public StatementPosition withConverter(Function<Object, Object> valueConverter) {
        Function<Object, Object> combined = this.valueConverter.andThen(valueConverter);
        return new StatementPosition(statement, type, position, combined);
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }

    public Object boundableValue(Object object) {
        return object != null ? valueConverter.apply(object) : null;
    }

    public PreparedStatement getStatement() {
        return statement;
    }
}
