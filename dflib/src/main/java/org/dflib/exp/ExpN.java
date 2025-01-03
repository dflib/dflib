package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * N-ary expression with multiple {@link Exp} arguments.
 */
public abstract class ExpN<T> implements Exp<T> {

    private final String opName;
    private final Class<T> type;
    protected final Exp<?>[] args;

    public ExpN(String opName, Class<T> type, Exp<?>... args) {
        this.opName = opName;
        this.type = type;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpN<?> expN = (ExpN<?>) o;
        return Objects.equals(opName, expN.opName)
                && Objects.equals(type, expN.type)
                && Objects.deepEquals(args, expN.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opName, type, Arrays.hashCode(args));
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
        return Arrays.stream(args).map(a -> a.toQL()).collect(Collectors.joining(",", opName + "(", ")"));
    }

    @Override
    public String toQL(DataFrame df) {
        return Arrays.stream(args).map(a -> a.toQL(df)).collect(Collectors.joining(",", opName + "(", ")"));
    }
}
