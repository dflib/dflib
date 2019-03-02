package com.nhl.dflib.jdbc.select;

import java.sql.PreparedStatement;

public class StatementBinder {

    private Binding[] bindings;

    public StatementBinder() {
    }

    public StatementBinder(Binding[] bindings) {
        this.bindings = bindings;
    }

    public void bind(PreparedStatement statement) {
        if (bindings != null) {
            for (int i = 0; i < bindings.length; i++) {
                bindings[i].bind(statement, i);
            }
        }
    }
}
