package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;

import java.util.Objects;

/**
 * A ternary expression with three {@link Exp} arguments.
 *
 * @since 2.0.0
 */
public abstract class Exp3<One, Two, Three, T> implements Exp<T> {

    private final String opName1;
    private final String opName2;
    private final Class<T> type;
    protected final Exp<One> one;
    protected final Exp<Two> two;
    protected final Exp<Three> three;

    public Exp3(String opName1, String opName2, Class<T> type, Exp<One> one, Exp<Two> two, Exp<Three> three) {
        this.opName1 = opName1;
        this.opName2 = opName2;
        this.type = type;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exp3<?, ?, ?, ?> exp3 = (Exp3<?, ?, ?, ?>) o;
        return Objects.equals(opName1, exp3.opName1)
                && Objects.equals(opName2, exp3.opName2)
                && Objects.equals(type, exp3.type)
                && Objects.equals(one, exp3.one)
                && Objects.equals(two, exp3.two)
                && Objects.equals(three, exp3.three);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opName1, opName2, type, one, two, three);
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
        return one.toQL() + " " + opName1 + " " + two.toQL() + " " + opName2 + " " + three.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return one.toQL(df) + " " + opName1 + " " + two.toQL(df) + " " + opName2 + " " + three.toQL(df);
    }
}
