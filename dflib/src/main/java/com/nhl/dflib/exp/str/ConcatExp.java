package com.nhl.dflib.exp.str;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.exp.map.MapExp1;
import com.nhl.dflib.exp.ExpN;

import java.util.Arrays;

import static com.nhl.dflib.Exp.$val;

/**
 * @since 0.11
 */
public class ConcatExp extends ExpN<String> implements StrExp {

    protected static Exp<String> cast(Exp<?> exp) {
        Class<?> t = exp.getType();
        return t.equals(String.class)
                ? (Exp<String>) exp
                : MapExp1.mapVal("castAsString", String.class, exp, String::valueOf);
    }

    public static StrExp forObjects(Object... valuesOrExps) {

        int len = valuesOrExps.length;
        if (len == 0) {
            // No exps to concat means null result
            return new StrConstExp(null);
        }

        for (Object v : valuesOrExps) {
            if (v == null) {
                // Any null argument to concat will produce null CONCAT result regardless of other values
                return new StrConstExp(null);
            }
        }

        Exp<String>[] args = Arrays.stream(valuesOrExps)
                .map(v -> v instanceof Exp ? cast((Exp<?>) v) : $val(v.toString()))
                .toArray(Exp[]::new);
        return new ConcatExp(args);
    }

    protected ConcatExp(Exp<String>[] args) {
        super("concat", String.class, args);
    }

    @Override
    protected Series<String> doEval(int height, Series<?>[] args) {

        StringBuilder row = new StringBuilder();
        ObjectAccumulator<String> accum = new ObjectAccumulator<>(height);

        for (int i = 0; i < height; i++) {
            row.setLength(0);
            boolean isNull = false;

            for (Series<?> arg : args) {
                Object val = arg.get(i);
                if (val == null) {
                    isNull = true;
                    break;
                } else {
                    row.append(val);
                }
            }

            accum.add(isNull ? null : row.toString());
        }

        return accum.toSeries();
    }
}
