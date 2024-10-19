package org.dflib.exp.map;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.exp.Exp3;
import org.dflib.f.Function3;

/**
 * @since 2.0.0
 */
public class MapExp3<One, Two, Three, T> extends Exp3<One, Two, Three, T> {

    public static <One, Two, Three, T> MapExp3<One, Two, Three, T> map(
            String opName1,
            String opName2,
            Class<T> type,
            Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Function3<Series<One>, Series<Two>, Series<Three>, Series<T>> op) {
        return new MapExp3<>(opName1, opName2, type, one, two, three, op);
    }

    public static <One, Two, Three, T> MapExp3<One, Two, Three, T> mapVal(
            String opName1,
            String opName2,
            Class<T> type,
            Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Function3<One, Two, Three, T> op) {
        return new MapExp3<>(opName1, opName2, type, one, two, three, valToSeries(op));
    }

    protected static <One, Two, Three, T> Function3<Series<One>, Series<Two>, Series<Three>, Series<T>> valToSeries(Function3<One, Two, Three, T> op) {
        return (s1, s2, s3) -> {
            int len = s1.size();
            ObjectAccum<T> accum = new ObjectAccum<>(len);
            for (int i = 0; i < len; i++) {
                One one = s1.get(i);
                Two two = s2.get(i);
                Three three = s3.get(i);
                accum.push(one != null && two != null && three != null ? op.apply(one, two, three) : null);
            }

            return accum.toSeries();
        };
    }

    private final Function3<Series<One>, Series<Two>, Series<Three>, Series<T>> op;

    protected MapExp3(
            String opName1,
            String opName2,
            Class<T> type, Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Function3<Series<One>, Series<Two>, Series<Three>, Series<T>> op) {
        super(opName1, opName2, type, one, two, three);
        this.op = op;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return doEval(one.eval(df), two.eval(df), three.eval(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(one.eval(s), two.eval(s), three.eval(s));
    }

    @Override
    public T reduce(DataFrame df) {
        return doEval(
                Series.ofVal(one.reduce(df), 1),
                Series.ofVal(two.reduce(df), 1),
                Series.ofVal(three.reduce(df), 1)
        ).get(0);
    }

    @Override
    public T reduce(Series<?> s) {
        return doEval(
                Series.ofVal(one.reduce(s), 1),
                Series.ofVal(two.reduce(s), 1),
                Series.ofVal(three.reduce(s), 1)
        ).get(0);
    }

    protected Series<T> doEval(Series<One> one, Series<Two> two, Series<Three> three) {
        return op.apply(one, two, three);
    }
}
