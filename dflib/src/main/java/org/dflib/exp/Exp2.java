package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;

import java.util.Objects;

/**
 * A binary expression with two {@link Exp} arguments.
 */
public abstract class Exp2<L, R, T> implements Exp<T> {

    protected final String opName;
    private final Class<T> type;
    protected final Exp<L> left;
    protected final Exp<R> right;

    public Exp2(String opName, Class<T> type, Exp<L> left, Exp<R> right) {
        this.opName = opName;
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exp2<?, ?, ?> exp2 = (Exp2<?, ?, ?>) o;
        return Objects.equals(opName, exp2.opName)
                && Objects.equals(type, exp2.type)
                && Objects.equals(left, exp2.left)
                && Objects.equals(right, exp2.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opName, type, left, right);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        return left.toQL() + " " + opName + " " + right.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return left.toQL(df) + " " + opName + " " + right.toQL(df);
    }
}
