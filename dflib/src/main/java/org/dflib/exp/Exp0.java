package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;

import java.util.Objects;

/**
 * A base class for expressions that are not composed of other expressions.
 */
public abstract class Exp0<T> implements Exp<T> {

    private final String opName;
    private final Class<T> type;

    public Exp0(String opName, Class<T> type) {
        this.opName = opName;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exp0<?> exp0 = (Exp0<?>) o;
        return Objects.equals(opName, exp0.opName)
                && Objects.equals(type, exp0.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opName, type);
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
