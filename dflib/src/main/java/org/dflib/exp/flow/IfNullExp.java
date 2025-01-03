package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Exp;

import java.util.Objects;


// TODO: this exp doesn't fit our Exp templates,
//  as it does lazy eval. Is there a reasonable template for that?
public class IfNullExp<T> implements Exp<T> {

    private final Exp<T> exp;
    private final Exp<T> ifNullExp;

    public IfNullExp(Exp<T> exp, Exp<T> ifNullExp) {
        this.exp = exp;
        this.ifNullExp = ifNullExp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IfNullExp<?> ifNullExp1 = (IfNullExp<?>) o;
        return Objects.equals(exp, ifNullExp1.exp)
                && Objects.equals(ifNullExp, ifNullExp1.ifNullExp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exp, ifNullExp);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return exp.getType();
    }

    @Override
    public String toQL() {
        return "ifnull(" + exp.toQL() + "," + ifNullExp.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "ifnull(" + exp.toQL(df) + "," + ifNullExp.toQL(df) + ")";
    }

    @Override
    public Series<T> eval(DataFrame df) {
        Series<T> data = exp.eval(df);
        IntSeries nullsIndex = data.index(Objects::isNull);

        int nullsLen = nullsIndex.size();
        if (nullsLen == 0) {
            return data;
        }

        return evalMergeReplacements(data, ifNullExp.eval(df.rows(nullsIndex).select()), nullsIndex);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        Series<T> data = exp.eval(s);
        IntSeries nullsIndex = data.index(Objects::isNull);

        if (nullsIndex.size() == 0) {
            return data;
        }

        return evalMergeReplacements(data, ifNullExp.eval(s.select(nullsIndex)), nullsIndex);
    }

    @Override
    public T reduce(DataFrame df) {
        throw new UnsupportedOperationException("IF expression '" + getType().getSimpleName() + " does not define a 'reduce' operation");
    }

    @Override
    public T reduce(Series<?> s) {
        throw new UnsupportedOperationException("IF expression '" + getType().getSimpleName() + " does not define a 'reduce' operation");
    }

    protected Series<T> evalMergeReplacements(Series<T> data, Series<T> nullReplacements, IntSeries nullsIndex) {
        // TODO: "data" is not a primitive Series by definition, but replacing nulls may produce a primitive-compatible
        //  Series. See if we can exploit this fact for performance optimization

        Object[] vals = new Object[data.size()];
        data.copyTo(vals, 0, 0, vals.length);

        int nullsLen = nullsIndex.size();

        for (int i = 0; i < nullsLen; i++) {
            vals[nullsIndex.getInt(i)] = nullReplacements.get(i);
        }

        return (Series<T>) Series.of(vals);
    }


}
