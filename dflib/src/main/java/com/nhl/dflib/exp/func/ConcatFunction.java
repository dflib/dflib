package com.nhl.dflib.exp.func;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.exp.SingleValueExp;
import com.nhl.dflib.exp.UnaryExp;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.nhl.dflib.Exp.$val;

/**
 * @since 0.11
 */
// TODO: generic NaryExp class?
public class ConcatFunction implements Exp<String> {

    protected static Exp<String> cast(Exp<?> exp) {
        Class<?> t = exp.getType();
        return t.equals(String.class)
                ? (Exp<String>) exp
                : new UnaryExp<>(exp, String.class, UnaryExp.toSeriesOp(String::valueOf));
    }

    private final Exp<String>[] args;

    public static Exp<String> forObjects(Object... valuesOrExps) {

        int len = valuesOrExps.length;
        if (len == 0) {
            // No exps to concat means null result
            return new SingleValueExp<>(null, String.class);
        }

        for (int i = 0; i < len; i++) {
            if (valuesOrExps[i] == null) {
                // Any null argument to concat will produce null CONCAT result regardless of other values
                return new SingleValueExp<>(null, String.class);
            }
        }

        Exp<String>[] args = Arrays.stream(valuesOrExps)
                .map(v -> v instanceof Exp ? cast((Exp<?>) v) : $val(v.toString()))
                .toArray(Exp[]::new);
        return new ConcatFunction(args);
    }

    protected ConcatFunction(Exp<String>[] args) {
        this.args = args;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getName() {
        return Arrays.stream(args).map(a -> a.getName()).collect(Collectors.joining(",", "concat(", ")"));
    }

    @Override
    public String getName(DataFrame df) {
        return Arrays.stream(args).map(a -> a.getName(df)).collect(Collectors.joining(",", "concat(", ")"));
    }

    @Override
    public Series<String> eval(DataFrame df) {

        int w = args.length;
        Series<String>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(df);
        }

        return concatColumns(df.height(), columns);
    }

    @Override
    public Series<String> eval(Series<?> s) {
        int w = args.length;
        Series<String>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(s);
        }

        return concatColumns(s.size(), columns);
    }

    protected Series<String> concatColumns(int h, Series<String>[] columns) {

        int w = args.length;

        StringBuilder row = new StringBuilder();
        ObjectAccumulator<String> accum = new ObjectAccumulator<>(h);

        for (int i = 0; i < h; i++) {
            row.setLength(0);
            boolean isNull = false;

            for (int j = 0; j < w; j++) {
                String val = columns[j].get(i);
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
