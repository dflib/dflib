package org.dflib.exp.map;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.BoolBuilder;
import org.dflib.exp.Exp3;
import org.dflib.f.Function3;
import org.dflib.f.Predicate3;

public class MapCondition3<One, Two, Three> extends Exp3<One, Two, Three, Boolean> implements Condition {

    public static <One, Two, Three>
    MapCondition3<One, Two, Three> map(
            String opName1,
            String opName2,
            Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Function3<Series<One>, Series<Two>, Series<Three>, BooleanSeries> op) {
        return new MapCondition3<>(opName1, opName2, one, two, three, op);
    }

    public static <One, Two, Three>
    MapCondition3<One, Two, Three> mapVal(
            String opName1,
            String opName2,
            Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Predicate3<One, Two, Three> predicate) {
        return new MapCondition3<>(opName1, opName2, one, two, three, valToSeries(predicate));
    }

    protected static <One, Two, Three>
    Function3<Series<One>, Series<Two>, Series<Three>, BooleanSeries> valToSeries(Predicate3<One, Two, Three> predicate) {
        return (s1, s2, s3) -> BoolBuilder.buildSeries(i -> {
            One one = s1.get(i);
            Two two = s2.get(i);
            Three three = s3.get(i);
            return one != null && two != null && three != null && predicate.test(one, two, three);
        }, s1.size());
    }

    private final Function3<Series<One>, Series<Two>, Series<Three>, BooleanSeries> op;

    protected MapCondition3(
            String opName1,
            String opName2,
            Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Function3<Series<One>, Series<Two>, Series<Three>, BooleanSeries> op) {
        super(opName1, opName2, Boolean.class, one, two, three);
        this.op = op;
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(one.eval(s), two.eval(s), three.eval(s));
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(one.eval(df), two.eval(df), three.eval(df));
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return doEval(
                Series.ofVal(one.reduce(df), 1),
                Series.ofVal(two.reduce(df), 1),
                Series.ofVal(three.reduce(df), 1)
        ).get(0);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return doEval(
                Series.ofVal(one.reduce(s), 1),
                Series.ofVal(two.reduce(s), 1),
                Series.ofVal(three.reduce(s), 1)
        ).get(0);
    }

    protected BooleanSeries doEval(Series<One> one, Series<Two> two, Series<Three> three) {
        return op.apply(one, two, three);
    }
}
