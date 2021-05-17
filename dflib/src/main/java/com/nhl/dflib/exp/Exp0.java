package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;

/**
 * A base class for expressions that are not composed from other expressions.
 *
 * @since 0.11
 */
public abstract class Exp0<T> implements Exp<T> {

    private final String opName;
    private final Class<T> type;

    public Exp0(String opName, Class<T> type) {
        this.opName = opName;
        this.type = type;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return opName;
    }

    @Override
    public String toQL(DataFrame df) {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
