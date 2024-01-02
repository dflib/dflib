package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.builder.ObjectAccum;
import org.dflib.exp.ExpN;

import static org.dflib.Exp.$val;

/**
 * @since 0.11
 */
public class ConcatExp extends ExpN<String> implements StrExp {

    public static StrExp forObjects(Object... valuesOrExps) {

        int len = valuesOrExps.length;
        if (len == 0) {
            // No exps to concat means null result
            return new StrConstExp(null);
        }

        Exp<?>[] args = new Exp[len];
        for (int i = 0; i < len; i++) {

            Object v = valuesOrExps[i];
            if (v == null) {
                // Any null argument to concat will produce null CONCAT result regardless of other values
                return new StrConstExp(null);
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
    protected Series<String> doEval(int height, Series<?>[] args) {

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
