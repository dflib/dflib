package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;

import java.util.Objects;

/**
 * A unary expression with an {@link Exp} argument.
 */
public abstract class Exp1<F, T> implements Exp<T> {

    protected final String opName;
    protected final Exp<F> exp;
    private final Class<T> type;

    public Exp1(String opName, Class<T> type, Exp<F> exp) {
        this.opName = opName;
        this.exp = exp;
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
        Exp1<?, ?> exp1 = (Exp1<?, ?>) o;
        return Objects.equals(opName, exp1.opName)
                && Objects.equals(exp, exp1.exp)
                && Objects.equals(type, exp1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opName, exp, type);
    }

    @Override
    public String toString() {
        return toQL();
    }

    // TODO: Exp1 and ExpN uses function syntax, Exp2 uses operator syntax... Unify, and deal with syntax changes in
    //  subclasses
    @Override
    public String toQL() {
        return opName + "(" + exp.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return opName + "(" + exp.toQL(df) + ")";
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
