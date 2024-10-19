package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.builder.ObjectAccum;
import org.dflib.exp.ExpN;

import static org.dflib.Exp.$val;


public class ConcatExp extends ExpN<String> implements StrExp {

    public static StrExp of(Object... valuesOrExps) {

        int len = valuesOrExps.length;
        if (len == 0) {
            // No exps to concat means null result
            return new StrScalarExp(null);
        }

        Exp<?>[] args = new Exp[len];
        for (int i = 0; i < len; i++) {

            Object v = valuesOrExps[i];
            if (v == null) {
                // Any null argument to concat will produce null CONCAT result regardless of other values
                return new StrScalarExp(null);
            } else if (v instanceof Exp) {
                args[i] = (Exp<?>) v;
            } else {
                args[i] = $val(v.toString());
            }
        }

        return new ConcatExp(args);
    }

    protected ConcatExp(Exp<?>[] args) {
        super("concat", String.class, args);
    }


    @Override
    public Series<String> eval(Series<?> s) {
        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(s);
        }

        return doEval(s.size(), columns);
    }

    @Override
    public Series<String> eval(DataFrame df) {
        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(df);
        }

        return doEval(df.height(), columns);
    }

    @Override
    public String reduce(Series<?> s) {
        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofVal(args[i].reduce(s), 1);
        }

        return doEval(s.size(), columns).get(0);
    }

    @Override
    public String reduce(DataFrame df) {
        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.ofVal(args[i].reduce(df), 1);
        }

        return doEval(df.height(), columns).get(0);
    }

    private Series<String> doEval(int height, Series<?>[] args) {

        StringBuilder row = new StringBuilder();
        ObjectAccum<String> accum = new ObjectAccum<>(height);

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

            accum.push(isNull ? null : row.toString());
        }

        return accum.toSeries();
    }
}
