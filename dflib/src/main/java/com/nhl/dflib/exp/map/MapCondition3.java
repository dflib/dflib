package com.nhl.dflib.exp.map;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.exp.Condition3;
import com.nhl.dflib.f.Function3;
import com.nhl.dflib.f.Predicate3;

/**
 * @since 1.0.0-M19
 */
public class MapCondition3<One, Two, Three> extends Condition3<One, Two, Three> {

    private final Function3<Series<One>, Series<Two>, Series<Three>, BooleanSeries> op;

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
        return (s1, s2, s3) -> {
            int len = s1.size();
            BoolAccum accum = new BoolAccum(len);
            for (int i = 0; i < len; i++) {
                One one = s1.get(i);
                Two two = s2.get(i);
                Three three = s3.get(i);
                accum.pushBool(one != null && two != null && three != null ? predicate.test(one, two, three) : false);
            }

            return accum.toSeries();
        };
    }

    protected MapCondition3(
            String opName1,
            String opName2,
            Exp<One> one,
            Exp<Two> two,
            Exp<Three> three,
            Function3<Series<One>, Series<Two>, Series<Three>, BooleanSeries> op) {
        super(opName1, opName2, one, two, three);
        this.op = op;
    }

    @Override
    protected BooleanSeries doEval(Series<One> one, Series<Two> two, Series<Three> three) {
        return op.apply(one, two, three);
    }
}
